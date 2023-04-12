package sala.xevi.myprivateshoppinglist.shoppinglist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sala.xevi.myprivateshoppinglist.MainActivity
import sala.xevi.myprivateshoppinglist.R
import sala.xevi.myprivateshoppinglist.createSelectableChip
import sala.xevi.myprivateshoppinglist.database.Category
import sala.xevi.myprivateshoppinglist.database.Product
import sala.xevi.myprivateshoppinglist.database.ProductWithCategories
import sala.xevi.myprivateshoppinglist.database.ShoppingListDatabase
import sala.xevi.myprivateshoppinglist.databinding.DialogAddCatToProductBinding
import sala.xevi.myprivateshoppinglist.databinding.DialogNewProductBinding
import sala.xevi.myprivateshoppinglist.databinding.FragmentShoppingListBinding
import sala.xevi.myprivateshoppinglist.filterProductWithCatList

class ShoppingListFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentShoppingListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_shopping_list, container, false)

        //The application context
        val application = requireNotNull(this.activity).application

        //The reference of the database
        val dataSource = ShoppingListDatabase.getInstance(application).shoppingListDao

        //With context and database, the viewModelFactory
        val viewModelFactory = ProductsViewModelFactory (dataSource, application)

        //And with the viewModelFactory, gets the viewModel
        val productsViewModel = ViewModelProvider(this, viewModelFactory)[ProductsViewModel::class.java]

        //sets categoriesViewModel layout variable <--not necessary
        //Not necessary. We only have a recyclerView i.ex binding.variableNameAtXml = categoriesViewModel

        //Sets the current activity as the lifecycle owner of the binding
        binding.lifecycleOwner = viewLifecycleOwner//this //viewLifeCycleOwner?


        val adapter = ProductsAdapter(
            ProductsItemListeners(
                {editText, productWithCategories ->
                    if (!editText.hasFocus() && editText.text.toString() != productWithCategories.product.name) {
                        val oldName = productWithCategories.product.name
                        productWithCategories.product.name = editText.text.toString()
                        productsViewModel.updateProduct(productWithCategories.product).invokeOnCompletion { handler->
                            CoroutineScope(Dispatchers.Main).launch{
                                handler?.let{
                                    Toast.makeText(context, getString(handler.message!!.toInt()), Toast.LENGTH_SHORT).show()
                                    productWithCategories.product.name = oldName
                                    editText.setText(oldName)
                                }
                            } }
                    }
                },
                {editText, productWithCategories ->
                    if (!editText.hasFocus() && editText.text.toString() != productWithCategories.product.comments) {
                        productWithCategories.product.comments = editText.text.toString()
                        productsViewModel.updateProduct(productWithCategories.product)
                    }
                },
                {product ->
                    var catList : List<Category>? = null
                    CoroutineScope(Dispatchers.IO).launch{
                        catList = productsViewModel.getCategoriesInProduct(product.idp)
                        productsViewModel.deleteProduct(product)}.invokeOnCompletion {
                            CoroutineScope(Dispatchers.Main).launch {
                                (activity as MainActivity).showSnackBar(
                                    Snackbar.make(binding.root, getString(R.string.deleted) + product.name, Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.undo)){
                                        productsViewModel.addNewProduct(product, catList)
                                    } )
                            }
                    }
                },
                {product, categoriesCG ->
                    (activity as MainActivity).showProgress(true)
                    var categories: List<Category>? = null
                    viewLifecycleOwner.lifecycleScope.launch {
                        categories = productsViewModel.getCategoriesNotInProduct(product.idp)
                    }.invokeOnCompletion {
                        (activity as MainActivity).showProgress(false)
                        addNewCategoryToItem(categories, productsViewModel, product, categoriesCG) }
                }
            ),
            productsViewModel
        )


        productsViewModel.productsWithCategories.observe(viewLifecycleOwner) {
            it?.let { adapter.submitList(it) }
        }


        binding.productsRV.adapter = adapter


        binding.newItemFAB.setOnClickListener {
            (activity as MainActivity).showProgress(true)
            var categories: List<Category>? = null
            viewLifecycleOwner.lifecycleScope.launch {
                categories = productsViewModel.getCategoriesList()
            }.invokeOnCompletion {
                (activity as MainActivity).showProgress(false)
                addNewItem(categories, productsViewModel) }
        }


        val onQueryTextListener = object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()){
                    productsViewModel.productsWithCategories.observe(viewLifecycleOwner) {
                        it?.let {
                            adapter.submitList(
                                filterProductWithCatList(
                                    it,
                                    newText,
                                    binding.filterButtonsTBG.checkedButtonIds.contains(binding.hasToBuyBtn.id),
                                    binding.filterButtonsTBG.checkedButtonIds.contains(binding.isUrgentBtn.id),
                                    binding.categoriesCG.children.map { view -> (view as Chip).text.toString() }
                                        .toList()
                                )
                            )

                        }
                    }
                } else {
                    productsViewModel.productsWithCategories.observe(viewLifecycleOwner) {
                        it?.let {
                            adapter.submitList(
                                filterProductWithCatList(
                                    it,
                                    newText,
                                    binding.filterButtonsTBG.checkedButtonIds.contains(binding.hasToBuyBtn.id),
                                    binding.filterButtonsTBG.checkedButtonIds.contains(binding.isUrgentBtn.id),
                                    binding.categoriesCG.children.map { view -> (view as Chip).text.toString() }
                                        .toList()
                                )
                            )

                        }
                    }
                }
                return false
            }
        }


        binding.searchProductSV.setOnQueryTextListener(onQueryTextListener)


        binding.filterIV.setOnClickListener{
            (activity as MainActivity).showProgress(true)
            var categories: List<Category>? = null
            viewLifecycleOwner.lifecycleScope.launch {
                categories = productsViewModel.getCategoriesList()
            }.invokeOnCompletion {
                (activity as MainActivity).showProgress(false)
                if (categories != null) {
                    addCatFilter(
                        categories!!,
                        onQueryTextListener,
                        binding.searchProductSV.query.toString(),
                        binding.categoriesCG
                    )
                }
            }
        }


        binding.filterButtonsTBG.addOnButtonCheckedListener { _,_,_ ->
            onQueryTextListener.onQueryTextChange(binding.searchProductSV.query.toString())
        }


        return binding.root
    }



    private fun addCatFilter(
        categories: List<Category>,listener: SearchView.OnQueryTextListener, text: String?, chipGroup: ChipGroup) {
        val bindingDialog = DialogAddCatToProductBinding.inflate(layoutInflater)

        AlertDialog.Builder(context).apply{
            setTitle(getString(R.string.add_cat_to_filter))
            setView(bindingDialog.root)
            bindingDialog.text.setText(R.string.add_cat_to_filter_detail)
            categories?.forEach{category ->
                bindingDialog.categoriesCG.addView(createSelectableChip(context, category.name))
            }

            setPositiveButton(getString(R.string.ok)){_,_->
                chipGroup.removeAllViews()
                bindingDialog.categoriesCG.children.filter{view-> bindingDialog.categoriesCG.checkedChipIds.contains(view.id)}
                    .forEach{view ->
                        (view as Chip).isCheckable = false
                        bindingDialog.categoriesCG.removeView(view)
                        chipGroup.addView(view)}
                listener.onQueryTextChange(text)
            }

            setNegativeButton(R.string.cancel) { _, _ -> }

            show()
        }
    }

    private fun addNewItem(categories: List<Category>?, productsViewModel: ProductsViewModel) {
        val bindingDialog = DialogNewProductBinding.inflate(layoutInflater)

        AlertDialog.Builder(context).apply {
            setTitle(R.string.create_new_cat)
            setView(bindingDialog.root)
            categories?.forEach { category->

                bindingDialog.categoriesCG.addView(createSelectableChip(context, category.name))
            }

            setPositiveButton(R.string.ok) { _, _ ->
                if (!bindingDialog.productET.text.isNullOrBlank()) {
                    val product = Product(
                        0,
                        bindingDialog.productET.text.toString(),
                        bindingDialog.commentsET.text.toString(),
                        bindingDialog.filterButtonsTBG.checkedButtonIds.contains(bindingDialog.hasToBuyBtn.id),
                        bindingDialog.filterButtonsTBG.checkedButtonIds.contains(bindingDialog.isUrgentBtn.id)
                    )

                    val categoriesStr =
                        arrayOfNulls<String>(bindingDialog.categoriesCG.checkedChipIds.size)
                    var i = 0
                    bindingDialog.categoriesCG.checkedChipIds.forEach { chip ->
                        categoriesStr[i++] =
                            (bindingDialog.categoriesCG.findViewById(chip) as Chip).text.toString()
                    }

                    productsViewModel.addNewProduct(product,
                        categories?.filter { category -> categoriesStr.contains(category.name) })
                        .invokeOnCompletion { handler->
                            CoroutineScope(Dispatchers.Main).launch{
                                handler?.let{ Toast.makeText(context, getString(handler.message!!.toInt()), Toast.LENGTH_SHORT).show() }}
                        }

                }
            }

            setNegativeButton(R.string.cancel) { _, _ -> }

            show()
        }
    }

    private fun addNewCategoryToItem(categories: List<Category>?, productsViewModel: ProductsViewModel, product: Product, categoriesCG: ChipGroup) {
        val bindingDialog = DialogAddCatToProductBinding.inflate(layoutInflater)

        AlertDialog.Builder(context).apply{
            setTitle(getString(R.string.add_cat_to_product, product.name))
            setView(bindingDialog.root)
            categories?.forEach{category ->
                bindingDialog.categoriesCG.addView(createSelectableChip(context, category.name))
            }

            setPositiveButton(getString(R.string.ok)){_,_->
                val selectedCategoriesStr = arrayOfNulls<String>(bindingDialog.categoriesCG.checkedChipIds.size)
                var i = 0
                bindingDialog.categoriesCG.checkedChipIds.forEach { chip ->
                    selectedCategoriesStr[i++] = (bindingDialog.categoriesCG.findViewById(chip) as Chip).text.toString()
                }

                productsViewModel.addCategoriesToProduct(product.idp, categories?.filter{category -> selectedCategoriesStr.contains(category.name)})
            }

            setNegativeButton(R.string.cancel) { _, _ -> }

            show()
        }
    }

    /*fun onButtonChecked(mbtg: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        println("hello")
    }*/
}