package sala.xevi.myprivateshoppinglist.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sala.xevi.myprivateshoppinglist.database.Category
import sala.xevi.myprivateshoppinglist.databinding.RvItemCategoryBinding
import sala.xevi.myprivateshoppinglist.databinding.RvItemProductBinding

class CategoriesAdapter (val listeners: CategoryItemListeners):
    ListAdapter<Category, CategoriesAdapter.ViewHolder>(CategoryDiffCallback()) {

    class ViewHolder(val binding: RvItemCategoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(category: Category, listeners: CategoryItemListeners){
            binding.category = category
            binding.categoryItemListeners = listeners
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listeners)
    }

}

/**
 * That class links the the functions passed as a param at the constructor to the functions
 * defined at the class. Constructor is called at [CategoriesFragment], with the creation of
 * [CategoriesAdapter] for the recyclerView.
 */
class CategoryItemListeners (
    val removeListener: (category: Category) -> Unit,
    val focusChangeCatETListener: (et: EditText, category: Category) -> Unit)
        {
            fun onClickRemove(category: Category) = removeListener(category)
            fun onFocusChangedCatNameET(editText: EditText, category: Category) = focusChangeCatETListener(editText, category)
}


/**
 * Necessary for ListAdapter for calculating de difference between two non-null items in a list.
 */
class CategoryDiffCallback: DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.idc == newItem.idc
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}

