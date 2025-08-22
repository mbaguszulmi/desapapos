package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desapabandara.pos.databinding.ItemProductSearchBinding
import com.desapabandara.pos.model.ui.PosProductDisplay

class ProductSearchAdapter(private val onItemClick: (String) -> Unit): ListAdapter<PosProductDisplay, ProductSearchAdapter.ProductViewHolder>(
    PRODUCT_DIFF
) {
    inner class ProductViewHolder(
        private val binding: ItemProductSearchBinding
    ): RecyclerView.ViewHolder(binding.root) {
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
        return ProductViewHolder(
            ItemProductSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}