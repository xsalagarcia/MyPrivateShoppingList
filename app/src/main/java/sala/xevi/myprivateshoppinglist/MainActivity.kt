package sala.xevi.myprivateshoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import sala.xevi.myprivateshoppinglist.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomNavigation.setOnItemSelectedListener { mi->onItemSelectedBottomNav(mi) }
    }



    fun onItemSelectedBottomNav(menuItem: MenuItem):Boolean {

        when (menuItem.itemId) {
            R.id.shopping_list -> findNavController(R.id.fragmentContainerView).navigate(R.id.shoppingListFragment)
            R.id.categories -> findNavController(R.id.fragmentContainerView).navigate(R.id.categoriesFragment)
            R.id.about -> findNavController(R.id.fragmentContainerView).navigate(R.id.aboutFragment)
        }


        return true
    }
}