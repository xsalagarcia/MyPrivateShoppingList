package sala.xevi.myprivateshoppinglist.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {

    @Insert
    fun insertProduct (product: Product)

    @Insert
    fun insertCategory (category: Category)

    @Insert
    fun insertProductCategoryCrossRef (categoryCrossRef: ProductCategoryCrossRef)

    @Update
    fun updateProduct (product: Product)

    @Update
    fun updateCategory (category: Category)

    @Delete
    fun deleteProduct (product: Product)

    @Delete
    fun deleteCategory (category: Category)

    @Delete
    fun deleteProductCategoryCrossRef (productCategoryCrossRef: ProductCategoryCrossRef)

    @Query("DELETE FROM product_category_cross_ref WHERE product_id = :productId AND category_id = :categoryId")
    fun deleteProductCategoryCrossRef (productId: Long, categoryId: Long)

    @Query("SELECT * FROM products_table ORDER BY id")
    fun getAllProducts (): List<Product>//LiveData<List<Product>>

    @Query("SELECT * FROM category_table")
    fun getAllCategories (): LiveData<List<Category>>

    @Query("SELECT * FROM product_category_cross_ref")
    fun getAllProductCategoryCrossRef (): List<ProductCategoryCrossRef>

    @Query("SELECT COUNT(*) FROM products_table")
    fun getProductsSize(): Int

    @Query("SELECT COUNT(*) FROM category_table")
    fun getCategoriesSize(): Int

    @Query("SELECT COUNT(*) FROM product_category_cross_ref")
    fun getProductCategoryCrossRefSize(): Int


    @Query("SELECT * FROM products_table WHERE id = :id LIMIT 1")
    fun getProductById(id: Long): Product?

    @Query("SELECT * FROM category_table WHERE id = :id LIMIT 1")
    fun getCategoryById(id: Long): Category?


    @Query("SELECT category_table.name FROM category_table " +
            "INNER JOIN product_category_cross_ref ON " +
            "product_category_cross_ref.category_id = category_table.id WHERE " +
            "product_category_cross_ref.product_id = :id")
    fun getCategoryNamesFromProductId(id: Long): List<String>

    @Query("SELECT category_table.id, category_table.name FROM category_table " +
            "INNER JOIN product_category_cross_ref ON " +
            "product_category_cross_ref.category_id = category_table.id WHERE " +
            "product_category_cross_ref.product_id = :id")
    fun getCategoriesFromProductId(id: Long): List<Category>

}