package com.desapabandara.pos.base.model

import java.util.Date

data class OrderPrintJob(
    val order: Order,
    val reprint: Boolean = false,
    val schedulePrint: Date? = null,
    val receiptOnly: Boolean = false
)
