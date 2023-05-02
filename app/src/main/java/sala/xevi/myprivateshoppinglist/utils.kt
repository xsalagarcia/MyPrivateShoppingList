package sala.xevi.myprivateshoppinglist

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import com.google.android.material.chip.Chip
import sala.xevi.myprivateshoppinglist.database.ProductWithCategories


/**
 * Creates a checkable chip without checked icon chip.
 */
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

/**
 * Creates a chip with close icon visible.
 */
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

/**
 * Given a list of products, returns another list based on a filter.
 * @param text for product name.
 * @param hasToBuy if true, only true values.
 * @param isUrgent if true, only true values.
 * @param listCat List of categories. If it isn't empty, products with these categories associated.
 */
fun filterProductWithCatList(list: List<ProductWithCategories>, text: String?, hasToBuy: Boolean, isUrgent: Boolean, listCat: List<String> ): List<ProductWithCategories>{
    var filteredList =list
    if (!text.isNullOrBlank()) {
        filteredList = filteredList.filter { filtered -> filtered.product.name.contains(text, true) }
    }

    if (isUrgent){
        filteredList = filteredList.filter {filtered ->
            filtered.product.isUrgent         }
    } else if (hasToBuy) {
        filteredList = filteredList.filter { filtered ->
            filtered.product.hasToShop
        }
    }

    if (listCat.isNotEmpty()) {
        filteredList = filteredList.filter { filtered ->
            filtered.categories.map { cat -> cat.name }.any { catName -> listCat.contains(catName) }// containsAll(listCat)
        }
    }
    return filteredList
}

/**
 * Defines the bounds of the view on the screen and check
 * if it contains the [point]
 * @param view The view to that will be used to check
 * @param point The point to check
 * @return true if the point is into the view bounds.
 */
fun isPointInsideViewBounds(view: View, point: Point): Boolean{
    val rect = Rect()
    view.getDrawingRect(rect)
    /*
    Creates an array of 2, passes the array to view.getLocationOnScreen
    and the array contains x and y coordinates on screen.
    x and y coordinates will be passed to offset the screen location
     */
    IntArray(2).also{xy->
        view.getLocationOnScreen(xy)
        rect.offset(xy[0], xy[1])
    }
    return rect.contains(point.x, point.y)
}