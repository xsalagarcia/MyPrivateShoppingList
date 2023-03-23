package sala.xevi.myprivateshoppinglist.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import sala.xevi.myprivateshoppinglist.R
import sala.xevi.myprivateshoppinglist.databinding.FragmentShoppingListBinding

class ShoppingListFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentShoppingListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_shopping_list, container, false)

        return binding.root
    }
}