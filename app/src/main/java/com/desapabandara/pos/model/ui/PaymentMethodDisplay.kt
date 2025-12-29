package com.desapabandara.pos.model.ui

import androidx.annotation.DrawableRes

data class PaymentMethodDisplay(
    val id: String,
    val name: String,
    val paymentMethodType: Int,
    @DrawableRes
    val logo: Int,
)