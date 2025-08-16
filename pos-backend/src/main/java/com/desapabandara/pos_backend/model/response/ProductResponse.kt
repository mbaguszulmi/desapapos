package com.desapabandara.pos_backend.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ProductResponse(
    val id: String = "",
    val name: String = "",
    @SerializedName("product_code")
    val productCode: String = "",
    val description: String = "",
    @SerializedName("price_excluding_tax")
    val priceExcludingTax: Double = .0,
    val tax: Double = .0,
    @SerializedName("is_tax_inclusive")
    val isTaxInclusive: Boolean = true,
    val cost: Double = .0,
    val stock: Double = .0,
    @SerializedName("preparing_duration")
    val preparingDuration: Int = 0,
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("store_id")
    val storeId: String = "",
    @SerializedName("category_id")
    val categoryId: String = "",
    @SerializedName("location_id")
    val locationId: String = "",
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("is_ingredient_only")
    val isIngredientOnly: Boolean = false,
    @SerializedName("created_at")
    val createdAt: Date = Date(),
    @SerializedName("deleted_at")
    val deletedAt: Date? = null,
    @SerializedName("updated_at")
    val updatedAt: Date = Date(),
)
