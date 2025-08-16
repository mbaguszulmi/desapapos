package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.desapabandara.pos.databinding.ItemPrinterBinding
import com.desapabandara.pos.model.ui.PrinterDisplay

class PrinterListAdapter(
    private val onPrinterConnect: (String) -> Unit,
    private val onPrintTestPage: (String) -> Unit,
    private val onDeletePrinter: (String) -> Unit
): ListAdapter<PrinterDisplay, PrinterListAdapter.PrinterViewHolder>(PRINTER_DIFF) {
    inner class PrinterViewHolder(
        private val binding: ItemPrinterBinding
    ): ViewHolder(binding.root) {

        fun bind(item: PrinterDisplay) {
            binding.apply {
                printer = item
                btnConnect.setOnClickListener {
                    onPrinterConnect(item.id)
                    swipeView.close(true, false)
                }

                btnTestPrint.setOnClickListener {
                    onPrintTestPage(item.id)
                    swipeView.close(true, false)
                }

                btnRemoveItem.setOnClickListener {
                    onDeletePrinter(item.id)
                    swipeView.close(true, false)
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrinterViewHolder {
        return PrinterViewHolder(ItemPrinterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: PrinterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

val PRINTER_DIFF = object: DiffUtil.ItemCallback<PrinterDisplay>() {
    override fun areItemsTheSame(oldItem: PrinterDisplay, newItem: PrinterDisplay): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PrinterDisplay, newItem: PrinterDisplay): Boolean {
        return oldItem == newItem
    }

}
