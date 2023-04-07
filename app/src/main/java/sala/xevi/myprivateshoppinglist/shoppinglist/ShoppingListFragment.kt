package sala.xevi.myprivateshoppinglist.shoppinglist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import sala.xevi.myprivateshoppinglist.MainActivity
import sala.xevi.myprivateshoppinglist.R
import sala.xevi.myprivateshoppinglist.createSelectableChip
import sala.xevi.myprivateshoppinglist.database.Category
import sala.xevi.myprivateshoppinglist.database.Product
import sala.xevi.myprivateshoppinglist.database.ShoppingListDatabase
import sala.xevi.myprivateshoppinglist.databinding.DialogNewProductBinding
import sala.xevi.myprivateshoppinglist.databinding.FragmentShoppingListBinding

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
                        productWithCategories.product.name = editText.text.toString()
                        productsViewModel.updateProduct(productWithCategories.product)
                    }
                },
                {editText, productWithCategories ->
                    if (!editText.hasFocus() && editText.text.toString() != productWithCategories.product.comments) {
                        productWithCategories.product.comments = editText.text.toString()
                        productsViewModel.updateProduct(productWithCategories.product)
                    }
                },
                {product ->
                    productsViewModel.deleteProduct(product)
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

        return binding.root
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

                val product = Product(
                    0,
                    bindingDialog.productET.text.toString(),
                    bindingDialog.commentsET.text.toString(),
                    bindingDialog.filterButtonsTBG.checkedButtonIds.contains(bindingDialog.hasToBuyBtn.id),
                    bindingDialog.filterButtonsTBG.checkedButtonIds.contains(bindingDialog.isUrgentBtn.id)
                )

                val categoriesStr = arrayOfNulls<String>(bindingDialog.categoriesCG.checkedChipIds.size)
                var i = 0
                bindingDialog.categoriesCG.checkedChipIds.forEach{chip ->
                    categoriesStr[i++] = (bindingDialog.categoriesCG.findViewById(chip) as Chip).text.toString()
             }

                productsViewModel.addNewProduct(product,
                    categories?.filter{category -> categoriesStr.contains(category.name)})

            }

            setNegativeButton(R.string.cancel) { _, _ -> }

            show()
        }
    }

    fun onButtonChecked(mbtg: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        println("hello")
    }
}