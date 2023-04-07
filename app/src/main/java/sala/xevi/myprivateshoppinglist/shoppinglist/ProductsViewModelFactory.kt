package sala.xevi.myprivateshoppinglist.shoppinglist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sala.xevi.myprivateshoppinglist.database.ShoppingListDao
import java.lang.IllegalArgumentException

class ProductsViewModelFactory (
    private val dataSource: ShoppingListDao,
    private val application: Application) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            return ProductsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}