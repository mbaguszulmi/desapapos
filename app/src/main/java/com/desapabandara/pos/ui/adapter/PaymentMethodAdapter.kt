package com.desapabandara.pos.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desapabandara.pos.databinding.ItemPaymentMethodBinding
import com.desapabandara.pos.model.ui.PaymentMethodDisplay

class PaymentMethodAdapter(
    private val onPaymentMethodSelected: (PaymentMethodDisplay) -> Unit
): ListAdapter<PaymentMethodDisplay, PaymentMethodAdapter.PaymentMethodViewHolder>(PAYMENT_METHOD_DIFF) {
    inner class PaymentMethodViewHolder(
        private val binding: ItemPaymentMethodBinding
    ): RecyclerView.ViewHolder(binding.root
    ) {
        fun bind(item: PaymentMethodDisplay) {
            binding.apply {
                paymentMethod = item
                root.setOnClickListener {
                    onPaymentMethodSelected(item)
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PaymentMethodViewHolder {
        return PaymentMethodViewHolder(
            ItemPaymentMethodBinding.inflate(
                android.view.LayoutInflater.from(parent.context),
                parent,
                false
            ),
        )
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private val PAYMENT_METHOD_DIFF = object: DiffUtil.ItemCallback<PaymentMethodDisplay>() {
    override fun areItemsTheSame(oldItem: PaymentMethodDisplay, newItem: PaymentMethodDisplay): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PaymentMethodDisplay, newItem: PaymentMethodDisplay): Boolean {
        return oldItem == newItem
    }
}
