package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desapabandara.pos.databinding.ItemOrderHistoryBinding
import com.desapabandara.pos.model.ui.HeldOrderDisplay

class OrderHistoryAdapter(
    private val onShowOrderClick: (String) -> Unit
): ListAdapter<HeldOrderDisplay, OrderHistoryAdapter.HeldOrderViewHolder>(ORDER_DISPLAY_DIFF) {
    inner class HeldOrderViewHolder(
        private val binding: ItemOrderHistoryBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HeldOrderDisplay) {
            binding.apply {
                order = item
                root.setOnClickListener {
                    onShowOrderClick(item.id)
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeldOrderViewHolder {
        return HeldOrderViewHolder(
            ItemOrderHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HeldOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}