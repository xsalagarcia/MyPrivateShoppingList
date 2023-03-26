package sala.xevi.myprivateshoppinglist.categories

import android.app.AlertDialog
import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sala.xevi.myprivateshoppinglist.R
import sala.xevi.myprivateshoppinglist.database.Category
import sala.xevi.myprivateshoppinglist.database.Product
import sala.xevi.myprivateshoppinglist.database.ShoppingListDao
import sala.xevi.myprivateshoppinglist.databinding.DialogNewCategoryBinding
import java.util.*

class CategoriesViewModel (val database: ShoppingListDao, application: Application
    ) : AndroidViewModel(application) {

    //database.getAllCategories returns a LiveData
    val categories = database.getAllCategories()

    fun addNewCategory(categoryName: String) {
        viewModelScope.launch {
            database.insertCategory(Category(0, categoryName))
        }
    }

    fun removeCategory(category: Category) {
        viewModelScope.launch {
            database.deleteCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch{
            database.updateCategory(category)
        }
    }


}