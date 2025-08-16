package com.desapabandara.pos_backend.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class StaffResponse(
    val id: String = "",
    val name: String = "",
    val pin: String = "",
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("multishift")
    val multiShift: Boolean = false,
    @SerializedName("phone_number")
    val phoneNumber: String = "",
    @SerializedName("position_id")
    val positionId: String = "",
    @SerializedName("avatar_url")
    val avatarUrl: String = "",
    @SerializedName("created_at")
    val createdAt: Date = Date(),
    @SerializedName("deleted_at")
    val deletedAt: Date? = null,
    @SerializedName("updated_at")
    val updatedAt: Date = Date(),
)
