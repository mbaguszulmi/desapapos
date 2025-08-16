package com.desapabandara.pos_backend.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PaymentMethodResponse(
    val id: String = "",
    val name: String = "",
    @SerializedName("payment_method_type")
    val paymentMethodType: Int = 0,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("created_at")
    val createdAt: Date = Date(),
    @SerializedName("deleted_at")
    val deletedAt: Date? = null,
    @SerializedName("updated_at")
    val updatedAt: Date = Date(),
)
