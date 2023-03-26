package sala.xevi.myprivateshoppinglist.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import sala.xevi.myprivateshoppinglist.database.Category
import sala.xevi.myprivateshoppinglist.databinding.RvItemCategoryBinding
import sala.xevi.myprivateshoppinglist.databinding.RvItemProductBinding

class CategoriesAdapter (val listeners: CategoryItemListeners):
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder> () {

    var data = listOf<Category>()
        set(category) {
            field = category
            notifyDataSetChanged()
        }

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
        holder.bind(data[position], listeners)
    }

    override fun getItemCount(): Int = data.size



}

class CategoryItemListeners (
    val removeListener: (category: Category) -> Unit,
    val focusChangeCatETListener: (et: EditText, category: Category) -> Unit)
    //
        {
            fun onClickRemove(category: Category) = removeListener(category)
            fun onFocusChangedCatNameET(editText: EditText, category: Category) = focusChangeCatETListener(editText, category)
}