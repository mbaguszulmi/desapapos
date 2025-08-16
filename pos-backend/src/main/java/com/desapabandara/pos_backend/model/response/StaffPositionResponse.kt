package com.desapabandara.pos_backend.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class StaffPositionResponse(
    val id: String = "",
    val name: String = "",
    @SerializedName("location_id")
    val locationId: String = "",
    @SerializedName("created_at")
    val createdAt: Date = Date(),
    @SerializedName("deleted_at")
    val deletedAt: Date? = null,
    @SerializedName("updated_at")
    val updatedAt: Date = Date(),
)
