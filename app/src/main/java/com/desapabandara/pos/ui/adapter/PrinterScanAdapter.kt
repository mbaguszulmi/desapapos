package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.desapabandara.pos.databinding.ItemPrinterScanBinding
import com.desapabandara.pos.printer.model.PrinterDeviceScanDisplay

class PrinterScanAdapter(
    private val onDeviceSelected: (PrinterDeviceScanDisplay) -> Unit
): ListAdapter<PrinterDeviceScanDisplay, PrinterScanAdapter.PrinterScanHolder>(PRINTER_SCAN_DIFF) {
    inner class PrinterScanHolder(
        private val binding: ItemPrinterScanBinding
    ): ViewHolder(binding.root) {
        fun bind(item: PrinterDeviceScanDisplay) {
            binding.apply {
                printer = item
                root.setOnClickListener {
                    onDeviceSelected(item)
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrinterScanHolder {
        return PrinterScanHolder(ItemPrinterScanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: PrinterScanHolder, position: Int) {
        return holder.bind(getItem(position))
    }
}

val PRINTER_SCAN_DIFF = object: DiffUtil.ItemCallback<PrinterDeviceScanDisplay>() {
    override fun areItemsTheSame(
        oldItem: PrinterDeviceScanDisplay,
        newItem: PrinterDeviceScanDisplay
    ): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(
        oldItem: PrinterDeviceScanDisplay,
        newItem: PrinterDeviceScanDisplay
    ): Boolean {
        return oldItem.name == newItem.name && oldItem.address == newItem.address
    }

}
