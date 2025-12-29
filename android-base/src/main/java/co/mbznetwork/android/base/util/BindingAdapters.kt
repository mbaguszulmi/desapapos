package co.mbznetwork.android.base.util

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import co.mbznetwork.android.base.R
import co.mbznetwork.android.base.model.UiMessage
import com.bumptech.glide.Glide

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

@BindingAdapter("imageUrl", "centerCrop", requireAll = false)
fun setImageUrl(imageView: ImageView, url: String, isCenterCrop: Boolean = false) {
    imageView.apply {
        if (url.isBlank()) {
            setImageResource(R.drawable.ic_no_image)
        } else {
            Glide.with(context)
                .load(url)
                .run {
                    if (isCenterCrop) {
                        centerCrop()
                    } else {
                        centerInside()
                    }
                }.error(R.drawable.ic_no_image)
                .into(this)
        }
    }
}

@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resourceId: Int) {
    // You can add logic here, for instance, checking if the resourceId is valid
    if (resourceId != 0) { // Assuming 0 means no image
        imageView.setImageResource(resourceId)
    } else {
        // Set a placeholder or clear the image
        imageView.setImageDrawable(null)
    }
}
