package com.desapabandara.pos_backend.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TableResponse(
    val id: String = "",
    val name: String = "",
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("table_capacity")
    val tableCapacity: Int = 0,
    @SerializedName("created_at")
    val createdAt: Date = Date(),
    @SerializedName("deleted_at")
    val deletedAt: Date? = null,
    @SerializedName("updated_at")
    val updatedAt: Date = Date(),
)
