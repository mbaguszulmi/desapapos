package com.desapabandara.pos.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desapabandara.pos.databinding.ItemPrinterLocationSelectBinding
import com.desapabandara.pos.model.ui.PrinterLocationSelection

class PrinterLocationSelectionAdapter(
    private val onToggleSelection: (PrinterLocationSelection) -> Unit
): ListAdapter<PrinterLocationSelection, PrinterLocationSelectionAdapter.ViewHolder>(PRINTER_LOCATION_SELECTION_DIFF) {

    inner class ViewHolder(
        private val binding: ItemPrinterLocationSelectBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PrinterLocationSelection) {
            binding.apply {
                printerLocationSelection = item
                btnSelect.setOnClickListener {
                    onToggleSelection(item)
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPrinterLocationSelectBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private val PRINTER_LOCATION_SELECTION_DIFF = object : DiffUtil.ItemCallback<PrinterLocationSelection>() {
    override fun areItemsTheSame(
        oldItem: PrinterLocationSelection,
        newItem: PrinterLocationSelection
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: PrinterLocationSelection,
        newItem: PrinterLocationSelection
    ) = oldItem == newItem

}
