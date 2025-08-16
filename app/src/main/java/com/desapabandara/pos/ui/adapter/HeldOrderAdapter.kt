package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.desapabandara.pos.databinding.ItemHeldOrderBinding
import com.desapabandara.pos.model.ui.HeldOrderDisplay

class HeldOrderAdapter(
    private val onLoadOrderClick: (String) -> Unit
): ListAdapter<HeldOrderDisplay, HeldOrderAdapter.HeldOrderViewHolder>(HELD_ORDER_DIFF) {
    inner class HeldOrderViewHolder(
        private val binding: ItemHeldOrderBinding
    ): ViewHolder(binding.root) {
        fun bind(item: HeldOrderDisplay) {
            binding.apply {
                order = item
                btnLoadOrder.setOnClickListener {
                    onLoadOrderClick(item.id)
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeldOrderViewHolder {
        return HeldOrderViewHolder(ItemHeldOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: HeldOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private val HELD_ORDER_DIFF = object : DiffUtil.ItemCallback<HeldOrderDisplay>() {
    override fun areItemsTheSame(oldItem: HeldOrderDisplay, newItem: HeldOrderDisplay): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HeldOrderDisplay, newItem: HeldOrderDisplay): Boolean {
        return oldItem == newItem
    }

}
