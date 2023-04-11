package sala.xevi.myprivateshoppinglist

import android.content.Context
import com.google.android.material.chip.Chip
import sala.xevi.myprivateshoppinglist.database.ProductWithCategories

fun createSelectableChip(context: Context, text: String) : Chip {
    return Chip(context).apply{
        this.text = text
        chipBackgroundColor = resources.getColorStateList( R.color.chip_color_background, null)
        setTextColor(resources.getColorStateList( R.color.chip_color_text, null))
        chipStrokeWidth=5f
        chipStrokeColor = resources.getColorStateList( R.color.chip_color_text, null)
        isCheckable = true
        isCheckedIconVisible = false
    }

}

fun createProductWithCatChip(context: Context, text: String) : Chip {
    return Chip(context).apply {
        isCloseIconVisible = true
        isChecked = true
        isCheckable = false
        chipStrokeColor = resources.getColorStateList(R.color.chip_color_text, null)
        isCheckedIconVisible = false
        this.text = text
    }
}

fun filterProductWithCatList(list: List<ProductWithCategories>, text: String?, hasToBuy: Boolean, isUrgent: Boolean, listCat: List<String> ): List<ProductWithCategories>{
    var filteredList =list
    if (!text.isNullOrBlank()) {
        filteredList = filteredList.filter { filtered -> filtered.product.name.contains(text) }
    }
    if (hasToBuy) {
        filteredList = filteredList.filter { filtered ->
            filtered.product.hasToShop == hasToBuy && filtered.product.isUrgent == isUrgent
        }
    }
    if (listCat.isNotEmpty()) {
        filteredList = filteredList.filter { filtered ->
            filtered.categories.map { cat -> cat.name }.containsAll(listCat)
        }
    }
    return filteredList
}