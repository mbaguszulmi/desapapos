package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desapabandara.pos.databinding.ItemCategoryPosBinding
import com.desapabandara.pos.model.ui.ProductCategoryDisplay

class CategoryPosAdapter(
    private val onItemSelected: (String) -> Unit
): ListAdapter<ProductCategoryDisplay, CategoryPosAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(
        private val binding: ItemCategoryPosBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductCategoryDisplay) {
            binding.apply {
                category = item
                root.setOnClickListener {
                    onItemSelected(item.id)
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryPosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductCategoryDisplay>() {
    override fun areItemsTheSame(
        oldItem: ProductCategoryDisplay,
        newItem: ProductCategoryDisplay
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ProductCategoryDisplay,
        newItem: ProductCategoryDisplay
    ) = oldItem == newItem

}
