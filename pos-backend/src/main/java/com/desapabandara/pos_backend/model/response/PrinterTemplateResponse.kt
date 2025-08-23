package com.desapabandara.pos_backend.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PrinterTemplateResponse(
    val id: String = "",
    val name: String = "",
    val template: String = "",
    @SerializedName("store_id")
    val storeId: String = "",
    val type: Int = 4,
    @SerializedName("created_at")
    val createdAt: Date = Date(),
    @SerializedName("deleted_at")
    val deletedAt: Date? = null,
    @SerializedName("updated_at")
    val updatedAt: Date = Date(),
)
