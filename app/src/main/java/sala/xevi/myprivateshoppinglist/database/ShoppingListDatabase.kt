package sala.xevi.myprivateshoppinglist.database

import android.content.Context
import androidx.room.*

/*
This file contains database and entities definition.
 */

@Entity(tableName = "category_table", indices = [Index(value = ["name"], unique = true)] )
data class Category (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="idc")
    var idc: Long = 0L,
    @ColumnInfo(name = "name")
    var name: String,

    )

@Entity(tableName = "products_table", indices = [Index(value = ["name"], unique = true)] )
data class Product (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="idp")
    var idp: Long = 0L,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "comments")
    var comments: String,
    @ColumnInfo(name = "has_to_shop")
    var hasToShop: Boolean,
    @ColumnInfo(name = "is_urgent")
    var isUrgent: Boolean,
)

@Entity(
    tableName = "product_category_cross_ref",
    primaryKeys = ["idp", "idc"],
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = arrayOf("idp"),
            childColumns = arrayOf("idp"),
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = Category::class,
            parentColumns = arrayOf("idc"),
            childColumns = arrayOf("idc"),
            onDelete = ForeignKey.CASCADE)
    ]
)
data class ProductCategoryCrossRef (
    @ColumnInfo(name = "idp")
    var productId: Long,
    @ColumnInfo(name = "idc", index = true)
    var categoryId: Long
)


//?to test
data class ProductWithCategories (
    @Embedded val product: Product,
    @Relation(
        parentColumn = "idp",
        entityColumn = "idc",
        associateBy = Junction(ProductCategoryCrossRef::class)
    )
    val categories: List<Category>
    )


@Database(entities = arrayOf(Category::class, Product::class, ProductCategoryCrossRef::class),
    version = 2, exportSchema = false)
abstract class ShoppingListDatabase : RoomDatabase(){

    abstract val shoppingListDao: ShoppingListDao

    companion object {

        /*
         * INSTANCE will keep a reference to any database returned via getInstance.
         *
         * This will help us avoid repeatedly initializing the database, which is expensive.
         *
         *  The value of a volatile variable will never be cached, and all writes and
         *  reads will be done to and from the main memory. It means that changes made by one
         *  thread to shared data are visible to other threads.
         */
        @Volatile
        private var INSTANCE: ShoppingListDatabase? = null

        fun getInstance(context: Context): ShoppingListDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null) { //if database doesn't exist
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ShoppingListDatabase::class.java,
                        "shopping_list_database")
                        .fallbackToDestructiveMigration().build() //no migration
                }
                return instance
            }
        }
    }
}