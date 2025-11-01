package com.desapabandara.pos.model.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class OrderItemDisplay {
    abstract val id: String

    @Parcelize
    data class Item(
        override val id: String,
        val name: String,
        val totalPrice: String,
        val quantity: String,
        val isTakeaway: Boolean
    ): OrderItemDisplay(), Parcelable

    @Parcelize
    data class ItemDetailed(
        override val id: String,
        val name: String,
        val totalPrice: String,
        val quantity: String,
        val isTakeaway: Boolean,
        val canChangeStatus: Boolean,
        val isPrepared: Boolean,
        val isServed: Boolean
    ): OrderItemDisplay(), Parcelable

    data class Note(
        override val id: String,
        val text: String,
    ): OrderItemDisplay()
}
