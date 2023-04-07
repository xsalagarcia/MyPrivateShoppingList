package sala.xevi.myprivateshoppinglist.shoppinglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import sala.xevi.myprivateshoppinglist.database.Category
import sala.xevi.myprivateshoppinglist.database.Product
import sala.xevi.myprivateshoppinglist.database.ProductCategoryCrossRef
import sala.xevi.myprivateshoppinglist.database.ShoppingListDao

class ProductsViewModel(val database: ShoppingListDao, application: Application) :
    AndroidViewModel(application) {

    val products = database.getAllProducts()

    val categories = database.getAllCategories()

    val productsWithCategories = database.getAllProductsWithCategories()

    suspend fun getCategoriesList() = database.getAListOfCategories()


    fun addNewProduct (product: Product, categories: List<Category>?) {

        CoroutineScope(Dispatchers.IO).launch{
            val productId = database.insertProduct(product)
            categories?.forEach { category ->
                   database.insertProductCategoryCrossRef( ProductCategoryCrossRef(productId, category.idc))
            }
        }
    }


    fun updateProduct (product: Product) {
        viewModelScope.launch {
            database.updateProduct(product)
        }
    }

    fun deleteProduct (product: Product) {
        viewModelScope.launch {
            database.deleteProduct(product)
        }
    }

    fun deleteProductCategoryCrossRef(product: Product, catName: String): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            database.deleteProductCategoryCrossRef(product.idp,catName)
        }

    }

    fun filterProductsByCategories (categories: List<Category>) : LiveData<List<Product>> {
        return database.getProductsByCategories(categories.map {cat -> cat.idc})
    }



}