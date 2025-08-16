package com.desapabandara.pos.model.ui

import com.desapabandara.pos.base.model.OrderType

sealed class SetOrderTypeResult {
    data object None: SetOrderTypeResult()

    data class Completed(
        val orderType: OrderType,
        val tableId: String,
    ): SetOrderTypeResult()
}