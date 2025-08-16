package com.desapabandara.pos.model.ui

sealed class OrderItemDisplay {
    abstract val id: String

    data class Item(
        override val id: String,
        val name: String,
        val totalPrice: String,
        val quantity: String,
        val isTakeaway: Boolean
    ): OrderItemDisplay()

    data class Note(
        override val id: String,
        val text: String,
    ): OrderItemDisplay()
}
