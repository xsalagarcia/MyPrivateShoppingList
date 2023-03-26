package sala.xevi.myprivateshoppinglist.categories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sala.xevi.myprivateshoppinglist.database.ShoppingListDao
import java.lang.IllegalArgumentException

class CategoriesViewModelFactory (
    private val dataSource: ShoppingListDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CategoriesViewModel::class.java)){
            return CategoriesViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
