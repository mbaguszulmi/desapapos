package com.desapabandara.pos.base.model

data class ItemInfo(
    val id: String,
    val quantity: Double,
    val note: String,
    val isTakeaway: Boolean
)
