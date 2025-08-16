package com.desapabandara.pos.model.ui

import com.desapabandara.pos.base.model.OrderType

data class HeldOrderDisplay(
    val id: String,
    val orderNumber: String,
    val orderDate: String,
    val orderType: OrderType,
    val orderTable: String,
    val total: String
)
