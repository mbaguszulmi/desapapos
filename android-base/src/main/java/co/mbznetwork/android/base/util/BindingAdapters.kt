package co.mbznetwork.android.base.util

import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import co.mbznetwork.android.base.model.UiMessage

@BindingAdapter("setMessage", requireAll = true)
fun setMessage(textView: TextView, message: UiMessage) {
    textView.text = when (message) {
        is UiMessage.ResourceMessage -> {
            if (message.formatArgs.isNotEmpty()) String.format(
                textView.context.getString(
                    message.id, *message.formatArgs
                )
            )
            else textView.context.getString(message.id)
        }
        is UiMessage.StringMessage -> message.message
    }
}