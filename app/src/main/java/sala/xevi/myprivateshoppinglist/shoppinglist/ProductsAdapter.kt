package sala.xevi.myprivateshoppinglist.shoppinglist

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import kotlinx.coroutines.*
import kotlinx.coroutines.NonDisposableHandle.parent
import sala.xevi.myprivateshoppinglist.createProductWithCatChip
import sala.xevi.myprivateshoppinglist.database.Product
import sala.xevi.myprivateshoppinglist.database.ProductWithCategories
import sala.xevi.myprivateshoppinglist.databinding.RvItemProductBinding

class ProductsAdapter (val listeners: ProductsItemListeners, private val viewModel: ProductsViewModel) : //viewModel for chip event.
    ListAdapter<ProductWithCategories, ProductsAdapter.ViewHolder> (ProductDiffCallback()) {


        class ViewHolder(val binding: RvItemProductBinding, val context: Context): RecyclerView.ViewHolder(binding.root) { //context for chip and and add cat event.

            fun bind (productWithCategories: ProductWithCategories,
                      listeners: ProductsItemListeners, viewModel: ProductsViewModel){ //viewModel for chipEvent

                binding.productWithCategories = productWithCategories
                binding.productsItemListeners = listeners
                binding.productViewModel = viewModel
                if (productWithCategories.product.hasToShop) {
                    binding.toBuyAndUrgentTBG.check(binding.hasToBuy.id)
                }
                if (productWithCategories.product.isUrgent) {
                    binding.toBuyAndUrgentTBG.check(binding.isUrgent.id)
                }

                productWithCategories.categories.forEach { cat ->
                    val chip = createProductWithCatChip(context, cat.name)
                    chip.setOnCloseIconClickListener { view ->
                        listeners.onCloseIconCategoryChip(view, productWithCategories.product, viewModel).invokeOnCompletion { handler->
                            view.findViewTreeLifecycleOwner()?.lifecycleScope?.launch{ //we need to execute it on UI-thread
                                if (handler == null) {
                                    binding.categoriesCG.removeView(view)
                                }
                            }

                        }
                    }
                    binding.categoriesCG.addView(chip)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(RvItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listeners, viewModel)
    }

}

class ProductDiffCallback: DiffUtil.ItemCallback<ProductWithCategories> (){
    override fun areItemsTheSame(oldItem: ProductWithCategories, newItem: ProductWithCategories): Boolean {
        return oldItem.product.idp == newItem.product.idp
    }

    override fun areContentsTheSame(oldItem: ProductWithCategories, newItem: ProductWithCategories): Boolean {
        return oldItem.product == newItem.product
    }

}

class ProductsItemListeners (
    val onFocusChangedProductETListener: (editText: EditText,  productWithCategories: ProductWithCategories) -> Unit,
    val onFocusChangedCommentsETListener: (editText: EditText,  productWithCategories: ProductWithCategories) -> Unit,
    val onClickRemoveListener: (product: Product) -> Unit,
    )  {

    fun onFocusChangedProductET (editText: EditText, productWithCategories: ProductWithCategories )
        = onFocusChangedProductETListener (editText, productWithCategories)

    fun onFocusChangedCommentsET(editText: EditText, productWithCategories: ProductWithCategories )
        = onFocusChangedCommentsETListener (editText, productWithCategories)

    fun onClickRemove(product: Product) = onClickRemoveListener(product)

    fun onCloseIconCategoryChip (view: View, product: Product, viewModel: ProductsViewModel): Job {
        return viewModel.deleteProductCategoryCrossRef(product, (view as Chip).text.toString())
    }
}