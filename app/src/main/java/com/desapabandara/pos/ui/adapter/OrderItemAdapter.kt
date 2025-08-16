package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desapabandara.pos.databinding.ItemOrderItemBinding
import com.desapabandara.pos.databinding.ItemOrderItemNoteBinding
import com.desapabandara.pos.model.ui.OrderItemDisplay

class OrderItemAdapter(
    private val onItemDelete: (String) -> Unit,
    private val onItemShowDetails: (String) -> Unit
): ListAdapter<OrderItemDisplay, OrderItemAdapter.ViewHolder>(ITEM_DIFF) {
    sealed class ViewHolder(root: View): RecyclerView.ViewHolder(root) {
        abstract fun bind(item: OrderItemDisplay)
    }

    inner class ItemHolder(
        private val binding: ItemOrderItemBinding
    ): ViewHolder(binding.root) {
        override fun bind(item: OrderItemDisplay) {
            binding.apply {
                orderItem = item as OrderItemDisplay.Item
                btnRemoveItem.setOnClickListener {
                    onItemDelete(item.id)
                    swipeView.close(true, false)
                }

                btnOrderDetails.setOnClickListener {
                    onItemShowDetails(item.id)
                    swipeView.close(true, false)
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
            TYPE_ITEM -> ItemHolder(ItemOrderItemBinding.inflate(
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
            is OrderItemDisplay.Item -> TYPE_ITEM
            else -> TYPE_NOTE
        }
    }
}

const val TYPE_ITEM = 1
const val TYPE_NOTE = 2

val ITEM_DIFF = object : DiffUtil.ItemCallback<OrderItemDisplay>(){
    override fun areItemsTheSame(oldItem: OrderItemDisplay, newItem: OrderItemDisplay): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OrderItemDisplay, newItem: OrderItemDisplay): Boolean {
        return oldItem == newItem
    }

}
