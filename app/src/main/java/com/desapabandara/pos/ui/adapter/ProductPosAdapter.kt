package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.desapabandara.pos.databinding.ItemPosProductBinding
import com.desapabandara.pos.model.ui.PosProductDisplay

class ProductPosAdapter(private val onItemClick: (String) -> Unit): ListAdapter<PosProductDisplay, ProductPosAdapter.ProductViewHolder>(PRODUCT_DIFF) {
    inner class ProductViewHolder(
        private val binding: ItemPosProductBinding
    ): ViewHolder(binding.root) {
        fun bind(item: PosProductDisplay) {
            binding.apply {
                product = item
                root.setOnClickListener {
                    onItemClick(item.id)
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemPosProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

val PRODUCT_DIFF = object: DiffUtil.ItemCallback<PosProductDisplay>() {
    override fun areItemsTheSame(oldItem: PosProductDisplay, newItem: PosProductDisplay): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PosProductDisplay,
        newItem: PosProductDisplay
    ): Boolean {
        return oldItem == newItem
    }

}
