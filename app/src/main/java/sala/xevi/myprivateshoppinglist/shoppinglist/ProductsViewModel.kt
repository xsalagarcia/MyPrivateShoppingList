package sala.xevi.myprivateshoppinglist.shoppinglist

import android.app.Application
import android.content.ContentResolver
import android.database.sqlite.SQLiteConstraintException
import android.provider.Settings.Global.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import sala.xevi.myprivateshoppinglist.R
import sala.xevi.myprivateshoppinglist.database.*

class ProductsViewModel(val database: ShoppingListDao, application: Application) :
    AndroidViewModel(application) {

    val products = database.getAllProducts()

    val categories = database.getAllCategories()

    val productsWithCategories = database.getAllProductsWithCategories()

    suspend fun getCategoriesList() = database.getAListOfCategories()

    suspend fun getCategoriesNotInProduct(idp: Long) = database.getAListOfCategoriesNotInProduct(idp)

    suspend fun getCategoriesInProduct(idp:Long) = database.getAListOfCategoriesInProduct(idp)


    fun addNewProduct (product: Product, categories: List<Category>?): Job {

        return CoroutineScope(Dispatchers.IO).launch{
            val result = try {
                val productId = database.insertProduct(product)
                categories?.forEach { category ->
                    database.insertProductCategoryCrossRef(
                        ProductCategoryCrossRef(
                            productId,
                            category.idc
                        )
                    )
                }
            } catch (e: Exception){
                cancel(if (e.javaClass == SQLiteConstraintException::class.java) (R.string.product_exist).toString()
                    else (R.string.unknown_problem).toString(), e)
            }
        }
    }

    fun addCategoriesToProduct(idp: Long, categories: List<Category>?){
        CoroutineScope(Dispatchers.IO).launch{
            categories?.forEach{cat ->
                database.insertProductCategoryCrossRef(ProductCategoryCrossRef(idp, cat.idc))
            }
        }

    }


    fun updateProduct (product: Product): Job {
        return viewModelScope.launch {
            try {
                database.updateProduct(product)
            } catch (e: Exception){
                cancel (if (e.javaClass == SQLiteConstraintException::class.java) (R.string.product_exist).toString()
                    else (R.string.unknown_problem).toString())
            }
        }
    }

    fun deleteProduct (product: Product): Job {
        return viewModelScope.launch {
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