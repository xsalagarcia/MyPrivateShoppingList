package sala.xevi.myprivateshoppinglist


import androidx.lifecycle.Lifecycle
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import sala.xevi.myprivateshoppinglist.database.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ShoppingListDatabaseTest {

    private lateinit var db: ShoppingListDatabase
    private lateinit var shoppingListDao: ShoppingListDao


    @Before //The first to execute
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        //in-memory database, the information stored here disappears when the process is killed
        db = Room.inMemoryDatabaseBuilder(context, ShoppingListDatabase::class.java)
            .allowMainThreadQueries() //allowing main thread queries, just for testing
            .build()

        shoppingListDao = db.shoppingListDao
    }

    @After //after the test
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertingProducts() {


        runBlocking {insertProducts()}

        assertEquals(shoppingListDao.getProductsSize(), 3)
        assertEquals(shoppingListDao.getProductById(1)!!.name, "mongetes")
        assertEquals(shoppingListDao.getProductById(3)!!.name, "Cigrons")
    }

    @Test
    @Throws(Exception::class)
    fun insertingCategories() {

        runBlocking{insertCategories()}

        assertEquals(shoppingListDao.getCategoriesSize(), 2)

    }

    @Test
    @Throws(Exception::class)
    fun insertingCrossRef(){

        insertingProducts()
        insertingCategories()
        val p1 = shoppingListDao.getProductById(1)
        val p2 = shoppingListDao.getProductById(2)
        val c1 = shoppingListDao.getCategoryById(1)
        val c2 = shoppingListDao.getCategoryById(2)

        shoppingListDao.insertProductCategoryCrossRef(ProductCategoryCrossRef(p1!!.idp, c1!!.idc))
        shoppingListDao.insertProductCategoryCrossRef(ProductCategoryCrossRef(p1!!.idp, c2!!.idc))

        val productWithCategories = shoppingListDao.getProductsWithCategoriesNoLiveData()

        val product1 = productWithCategories.filter{elem  -> elem.product.idp  == 1L }.first()

        assertEquals(product1.product.name, "mongetes")
        assertEquals(product1.categories.size, 2)
        assertEquals(product1.categories.contains(c1), true)
        assertEquals(product1.categories.contains(c2), true)

        assertEquals(shoppingListDao.getCategoryNamesFromProductId(p1!!.idp).size, 2)
        assertEquals(shoppingListDao.getCategoriesFromProductId(p1!!.idp).size, 2)

    }



    private suspend fun insertProducts() {
        val product1 = Product(0, "mongetes", "3kg", true, true)
        val product2 = Product (0, "patates", "1 bossa", true, false)
        val product3 = Product (0, "Cigrons", "2 pots", false, false)

        shoppingListDao.insertProduct(product1)//will be id = 1
        shoppingListDao.insertProduct(product2)//will be id = 2
        shoppingListDao.insertProduct(product3)//will be id = 3
    }

    private suspend fun insertCategories(){
        val cat1 = Category(0, "Bonpreu")
        val cat2 = Category(0, "Mercat")
        shoppingListDao.insertCategory(cat1)
        shoppingListDao.insertCategory(cat2)
    }
}