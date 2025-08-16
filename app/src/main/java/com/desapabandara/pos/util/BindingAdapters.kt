package com.desapabandara.pos.util

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.desapabandara.pos.R
import com.desapabandara.pos.base.model.OrderType
import com.desapabandara.pos.model.ui.SyncStatus

@BindingAdapter("isSelected")
fun isSelected(view: View?, isSelected: Boolean) {
    view?.isSelected = isSelected
}

@BindingAdapter("syncStatus")
fun setSyncStatus(textView: TextView, syncStatus: SyncStatus) {
    textView.apply {
        (when (syncStatus) {
            is SyncStatus.Syncing -> {
                setText(syncStatus.syncNameResource)
            }
            is SyncStatus.Error -> {
                text = syncStatus.message
            }
        })
    }
}

@BindingAdapter("orderTypeText")
fun setOrderTypeText(textView: TextView, orderType: OrderType) {
    textView.apply {
        text = context.getString(when (orderType) {
            OrderType.Takeaway -> R.string.order_type_takeaway
            else -> R.string.order_type_eat_in
        })
    }
}

@BindingAdapter("android:text")
fun setText(view: EditText, value: Int) {
    view.setText(value.toString())
}

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getText(view: EditText): Int {
    return try {
        view.text.toString().toInt()
    } catch (e: NumberFormatException) {
        0
    }
}
