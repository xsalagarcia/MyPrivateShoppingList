package sala.xevi.myprivateshoppinglist

import android.content.Context
import com.google.android.material.chip.Chip

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