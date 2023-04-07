package sala.xevi.myprivateshoppinglist.categories

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import sala.xevi.myprivateshoppinglist.R
import sala.xevi.myprivateshoppinglist.database.ShoppingListDatabase
import sala.xevi.myprivateshoppinglist.databinding.DialogNewCategoryBinding
import sala.xevi.myprivateshoppinglist.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCategoriesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false)

        //The application context
        val application = requireNotNull(this.activity).application

        //The reference of the database
        val dataSource = ShoppingListDatabase.getInstance(application).shoppingListDao

        //With context and database, the viewModelFactory
        val viewModelFactory = CategoriesViewModelFactory(dataSource, application)

        //And with the viewModelFactory, gets the viewModel
        val categoriesViewModel = ViewModelProvider(this, viewModelFactory)[CategoriesViewModel::class.java]

        //sets categoriesViewModel layout variable
        binding.categoriesViewModel = categoriesViewModel

        //Sets the current activity as the lifecycle owner of the binding
        binding.lifecycleOwner = viewLifecycleOwner//this //viewLifeCycleOwner??



        //and assigns the viewModel to the variable declared at xml??
        //Not necessary. We only have a recyclerView i.ex binding.variableNameAtXml = categoriesViewModel

        //Adapter. Creation, sets data with observer, sets adapter to the recyclerView.
        val adapter = CategoriesAdapter(

            CategoryItemListeners (
                    {category -> categoriesViewModel.removeCategory(category)},
                    {editText, category ->
                        if (!editText.hasFocus() && editText.text.toString() != category.name) {
                            category.name = editText.text.toString()
                            categoriesViewModel.updateCategory(category)
                        }
                    }
            )

        )

        categoriesViewModel.categories.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }


        binding.searchCategoriesSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrBlank()){
                        categoriesViewModel.categories.observe(viewLifecycleOwner, Observer{
                            it?.let{
                                adapter.submitList(it)
                            }
                        })
                    } else {
                        categoriesViewModel.filteredCategories (newText).observe(viewLifecycleOwner, Observer{
                            it?.let{
                                adapter.submitList(it)
                            }
                        })
                    }
                return false
            }

        })

        binding.categoriesRV.adapter = adapter

        //In a fragment, onclick programmatically.
        binding.newItemFAB.setOnClickListener { addCategory(categoriesViewModel) }

        return binding.root
    }

    /**
     * UI creation (AlertDialog) has to be at the fragment. New item creation at the viewModel (positiveButton).
     */
    private fun addCategory(categoriesViewModel: CategoriesViewModel) {
        val bindingDialog = DialogNewCategoryBinding.inflate(layoutInflater )
        AlertDialog.Builder(context).apply {
            setTitle(R.string.create_new_cat)
            setView(bindingDialog.root)
            setPositiveButton(R.string.ok){_, _ ->
                categoriesViewModel.addNewCategory(bindingDialog.categoryET.text.toString())
            }
            setNegativeButton(R.string.cancel) {_ , _ ->}
            show()
        }
    }


}