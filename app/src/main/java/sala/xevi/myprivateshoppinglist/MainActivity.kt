package sala.xevi.myprivateshoppinglist

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import sala.xevi.myprivateshoppinglist.databinding.ActivityMainBinding
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var snackbar: Snackbar? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

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

    fun showProgress (hasToShow: Boolean) {

        if (hasToShow) {
            binding.progressBar.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            binding.progressBar.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }


    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        //https://gist.github.com/and291/ce5704c4163f8dcd9d06b1ab6a9850cf

        //when snackbar isn't null, when is shown, runs...
        snackbar?.takeIf{it.isShown}?.run{
            val touchPoint = Point(ev!!.rawX.roundToInt(), ev.rawY.roundToInt())
            if (!isPointInsideViewBounds(view, touchPoint)){
                dismiss()
                snackbar = null
            }

        }

        return super.dispatchTouchEvent(ev)
    }

    fun showSnackBar(snackbar: Snackbar){
        this.snackbar = snackbar
        snackbar.show()
    }
}