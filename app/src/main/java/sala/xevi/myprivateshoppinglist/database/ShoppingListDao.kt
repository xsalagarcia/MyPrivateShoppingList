package sala.xevi.myprivateshoppinglist.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {

    @Insert
    suspend fun insertProduct (product: Product): Long

    @Insert
    suspend fun insertCategory (category: Category): Long

    @Insert
    suspend fun insertProductCategoryCrossRef (categoryCrossRef: ProductCategoryCrossRef)

    @Update
    suspend fun updateProduct (product: Product)

    @Update
    suspend fun updateCategory (category: Category)

    @Delete
    suspend fun deleteProduct (product: Product)

    @Delete
    suspend fun deleteCategory (category: Category)

    @Delete
    fun deleteProductCategoryCrossRef (productCategoryCrossRef: ProductCategoryCrossRef)

    @Query("DELETE FROM product_category_cross_ref WHERE idp = :productId AND idc = :categoryId")
    fun deleteProductCategoryCrossRef (productId: Long, categoryId: Long)

    @Query("DELETE FROM product_category_cross_ref WHERE idp = :productId AND product_category_cross_ref.idc IN " +
            "(SELECT category_table.idc FROM category_table WHERE name = :catName)")
    suspend fun deleteProductCategoryCrossRef (productId: Long, catName: String)

    @Query("SELECT * FROM products_table ORDER BY idp")
    fun getAllProducts (): LiveData<List<Product>>//LiveData<List<Product>>

    @Query("SELECT * FROM category_table ORDER BY idc")
    fun getAllCategories (): LiveData<List<Category>> //suspend not necessary with livedata. Use dispatchers.io

    @Query("SELECT * FROM category_table ORDER BY idc")
    suspend fun getAllCategoriesInAList(): List<Category>

    @Query("SELECT * FROM category_table ORDER BY idc")
    suspend fun getAListOfCategories(): List<Category>

    @Query("SELECT * FROM category_table WHERE name LIKE '%' || :filter || '%'")
    fun getFilteredNameCategories(filter: String): LiveData<List<Category>>

/*
    @Query("SELECT * FROM user WHERE region IN (:regions)")
    fun loadUsersFromRegions(regions: List<String>): List<User>
*/
    @Query("SELECT p.idp, p.name, p.comments, p.has_to_shop, p.is_urgent " +
            "FROM products_table AS p INNER JOIN product_category_cross_ref AS pc " +
            "ON p.idp = pc.idp " +
            "WHERE pc.idc IN (:categories) ORDER BY p.idp")
    fun getProductsByCategories(categories: List<Long>): LiveData<List<Product>>

    @Query("SELECT * FROM product_category_cross_ref")
    fun getAllProductCategoryCrossRef (): List<ProductCategoryCrossRef>

    @Query("SELECT COUNT(*) FROM products_table")
    fun getProductsSize(): Int

    @Query("SELECT COUNT(*) FROM category_table")
    fun getCategoriesSize(): Int

    @Query("SELECT COUNT(*) FROM product_category_cross_ref")
    fun getProductCategoryCrossRefSize(): Int


    @Query("SELECT * FROM products_table WHERE idp = :id LIMIT 1")
    fun getProductById(id: Long): Product?

    @Query("SELECT * FROM category_table WHERE idc = :id LIMIT 1")
    fun getCategoryById(id: Long): Category?


    @Query("SELECT category_table.name FROM category_table " +
            "INNER JOIN product_category_cross_ref ON " +
            "product_category_cross_ref.idc = category_table.idc WHERE " +
            "product_category_cross_ref.idp = :id")
    fun getCategoryNamesFromProductId(id: Long): List<String>

    @Query("SELECT category_table.idc, category_table.name FROM category_table " +
            "INNER JOIN product_category_cross_ref ON " +
            "product_category_cross_ref.idc = category_table.idc WHERE " +
            "product_category_cross_ref.idp = :id")
    fun getCategoriesFromProductId(id: Long): List<Category>


    @Transaction
    @Query("SELECT * FROM products_table")
    fun getAllProductsWithCategories():LiveData<List<ProductWithCategories>>

    @Transaction
    @Query("SELECT * FROM products_table")
    fun getProductsWithCategoriesNoLiveData():List<ProductWithCategories>

}