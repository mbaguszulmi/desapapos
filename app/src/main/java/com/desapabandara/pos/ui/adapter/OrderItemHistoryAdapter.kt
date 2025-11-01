package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desapabandara.pos.databinding.ItemOrderItemHistoryBinding
import com.desapabandara.pos.databinding.ItemOrderItemNoteBinding
import com.desapabandara.pos.model.ui.OrderItemDisplay

class OrderItemHistoryAdapter(
    private val onTogglePrepared: (OrderItemDisplay.ItemDetailed) -> Unit,
    private val onToggleServed: (OrderItemDisplay.ItemDetailed) -> Unit
): ListAdapter<OrderItemDisplay, OrderItemHistoryAdapter.ViewHolder>(ITEM_DIFF) {
    sealed class ViewHolder(root: View): RecyclerView.ViewHolder(root) {
        abstract fun bind(item: OrderItemDisplay)
    }

    inner class ItemHolder(
        private val binding: ItemOrderItemHistoryBinding
    ): ViewHolder(binding.root) {
        override fun bind(item: OrderItemDisplay) {
            binding.apply {
                orderItem = item as OrderItemDisplay.ItemDetailed
                btnTogglePrepared.setOnClickListener {
                    onTogglePrepared(item)
                }

                btnToggleServed.setOnClickListener {
                    onToggleServed(item)
                }

                executePendingBindings()
            }
        }
    }

    inner class NoteHolder(
        private val binding: ItemOrderItemNoteBinding
    ): ViewHolder(binding.root) {
        override fun bind(item: OrderItemDisplay) {
            binding.apply {
                note = (item as? OrderItemDisplay.Note)?.text ?: ""

                executePendingBindings()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> ItemHolder(ItemOrderItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
            else -> NoteHolder(ItemOrderItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is OrderItemDisplay.ItemDetailed -> TYPE_ITEM
            else -> TYPE_NOTE
        }
    }
}
