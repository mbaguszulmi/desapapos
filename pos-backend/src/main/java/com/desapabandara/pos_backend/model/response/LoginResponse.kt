package com.desapabandara.pos_backend.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CompanyResponse(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    @SerializedName("logo_url")
    val logoUrl: String = "",
    @SerializedName("created_at")
    val createdAt: Date = Date(),
    @SerializedName("deleted_at")
    val deletedAt: Date? = null,
    @SerializedName("updated_at")
    val updatedAt: Date = Date(),
)

data class StoreResponse(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    @SerializedName("company_id")
    val companyId: String = "",
    @SerializedName("is_default")
    val isDefault: Boolean = false,
    @SerializedName("store_username")
    val storeUsername: String = "",
    @SerializedName("store_password")
    val storePassword: String = "",
    val company: CompanyResponse = CompanyResponse(),
    @SerializedName("created_at")
    val createdAt: Date = Date(),
    @SerializedName("deleted_at")
    val deletedAt: Date? = null,
    @SerializedName("updated_at")
    val updatedAt: Date = Date(),
)

data class LoginResponse(
    val store: StoreResponse = StoreResponse(),
    val token: String = "",
    @SerializedName("refresh_token")
    val refreshToken: String = ""
)
