package sala.xevi.myprivateshoppinglist.shoppinglist

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import sala.xevi.myprivateshoppinglist.database.Product
import sala.xevi.myprivateshoppinglist.database.ProductWithCategories



@BindingAdapter("expandImageButton")
fun ImageButton.setExpandImageButton(view: View) {
    setOnClickListener {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("productCommentsTV")
fun TextView.setProductComments(product: Product) {

    if (product.comments.isNotBlank()) {
        visibility = TextView.VISIBLE
        text = product.comments
    } else {
        visibility = TextView.GONE
    }

}




@BindingAdapter("buttonUrgent","buttonToBuy")
fun MaterialButtonToggleGroup.setButtonsUrgentAndToBuy(urgentBtn: Button, toBuyBtn: Button) {
    urgentBtn.setOnClickListener {
        if (checkedButtonIds.contains(urgentBtn.id) && !checkedButtonIds.contains(toBuyBtn.id)){
            check(toBuyBtn.id)
        }
    }

    toBuyBtn.setOnClickListener {
        if(checkedButtonIds.contains(urgentBtn.id) && !checkedButtonIds.contains(toBuyBtn.id)){
            uncheck(urgentBtn.id)
        }
    }
}

//At search, the fragment.
@BindingAdapter("checkedButtonsAtSearch")
fun MaterialButtonToggleGroup.setCheckedButtonsAtSearch(toBuyBtn: Button) {
    addOnButtonCheckedListener{group,checkedId,isChecked->
        if (checkedButtonIds.size == 2  ){ //both are checked
            println("both checked")
        } else if (checkedButtonIds.size == 0){ //none is checked
            println("none checked")
        } else if (checkedId == toBuyBtn.id && checkedButtonIds.size == 1 && isChecked || checkedId != toBuyBtn.id && !isChecked) { //one is checked (to buy action)
            println("to buy checked")
        }
    }
}

//At recyclerview
@BindingAdapter("buttonToBuyAtChecked", "viewModel", "productWithCategories")
fun MaterialButtonToggleGroup.setCheckedButtonsAtItem(toBuyBtn: Button, viewModel: ProductsViewModel, productWithCategories: ProductWithCategories) {
    addOnButtonCheckedListener{group,checkedId,isChecked->
        if (checkedButtonIds.size == 2  ){ //both are checked
            productWithCategories.product.hasToShop = true
            productWithCategories.product.isUrgent = true
            viewModel.updateProduct(productWithCategories.product)
        } else if (checkedButtonIds.size == 0){ //none is checked
            productWithCategories.product.hasToShop = false
            productWithCategories.product.isUrgent = false
            viewModel.updateProduct(productWithCategories.product)
        } else if (checkedId == toBuyBtn.id && checkedButtonIds.size == 1 && isChecked || checkedId != toBuyBtn.id && !isChecked) { //one is checked (to buy action)
            productWithCategories.product.hasToShop = true
            productWithCategories.product.isUrgent = false
            viewModel.updateProduct(productWithCategories.product)
        }
    }


}


