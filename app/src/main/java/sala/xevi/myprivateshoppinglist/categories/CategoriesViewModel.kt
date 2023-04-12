package sala.xevi.myprivateshoppinglist.categories

import android.app.AlertDialog
import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
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


    fun filteredCategories(filter: String): LiveData<List<Category>> {
        return database.getFilteredNameCategories(filter)
    }

    fun addNewCategory(categoryName: String): Job {
        return viewModelScope.launch {
            try {
                database.insertCategory(Category(0, categoryName))
            } catch (e: Exception){
                cancel(if (e.javaClass == SQLiteConstraintException::class.java) (R.string.category_exist).toString()
                else (R.string.unknown_problem).toString(), e)
            }
        }
    }

    fun removeCategory(category: Category) : Job {
        return viewModelScope.launch {
            database.deleteCategory(category)
        }
    }

    fun updateCategory(category: Category): Job {
        return viewModelScope.launch{
            try{
                database.updateCategory(category)
            } catch (e: Exception) {
                cancel(if (e.javaClass == SQLiteConstraintException::class.java) (R.string.category_exist).toString()
                else (R.string.unknown_problem).toString(), e)
            }
        }
    }


}