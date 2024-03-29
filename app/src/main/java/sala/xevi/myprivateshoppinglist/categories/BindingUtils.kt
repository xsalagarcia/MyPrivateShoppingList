package sala.xevi.myprivateshoppinglist.categories

import android.widget.EditText
import androidx.databinding.BindingAdapter
import sala.xevi.myprivateshoppinglist.database.Category

/**
 * This file contains the custom binding adapters for categories fragment and the associated recycler view.
 */

@BindingAdapter("categoryName")
fun EditText.setCategoryNameString(item: Category) {
    item?.let {
        setText(item.name)
    }
}

