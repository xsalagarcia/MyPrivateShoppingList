package sala.xevi.myprivateshoppinglist.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import sala.xevi.myprivateshoppinglist.R
import sala.xevi.myprivateshoppinglist.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCategoriesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false)
        return binding.root
    }
}