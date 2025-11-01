package com.desapabandara.pos_backend.model.request

import com.google.gson.annotations.SerializedName
import java.util.Date

data class OrderRequest(
    var id: String = "",
    @SerializedName("store_id")
    var storeId: String = "",
    @SerializedName("invoice_number")
    var invoiceNumber: String = "",
    @SerializedName("order_number")
    var orderNumber: Int = 0,
    @SerializedName("adult_male_count")
    var adultMaleCount: Int = 0,
    @SerializedName("adult_female_count")
    var adultFemaleCount: Int = 0,
    @SerializedName("child_male_count")
    var childMaleCount: Int = 0,
    @SerializedName("child_female_count")
    var childFemaleCount: Int = 0,
    @SerializedName("order_type")
    var orderType: Int = 1,
    @SerializedName("subtotal_excluding_tax")
    var subtotalExcludingTax: Double = .0,
    @SerializedName("subtotal_tax")
    var subtotalTax: Double = .0,
    @SerializedName("discount_excluding_tax")
    var discountExcludingTax: Double = .0,
    @SerializedName("discount_tax")
    var discountTax: Double = .0,
    @SerializedName("surcharge_excluding_tax")
    var surchargeExcludingTax: Double = .0,
    @SerializedName("surcharge_tax")
    var surchargeTax: Double = .0,
    @SerializedName("total_excluding_tax")
    var totalExcludingTax: Double = .0,
    @SerializedName("total_tax")
    var totalTax: Double = .0,
    @SerializedName("total_cost")
    var totalCost: Double = .0,
    @SerializedName("order_status")
    var orderStatus: Int = 0,
    @SerializedName("payment_status")
    var paymentStatus: Int = 0,
    @SerializedName("expected_total_preparing_duration")
    var expectedTotalPreparingDuration: Int = 0,
    @SerializedName("created_by")
    var createdBy: String = "",
    @SerializedName("customer_name")
    var customerName: String = "",
    @SerializedName("order_note")
    var orderNote: String = "",
    @SerializedName("total_amount_tendered")
    var totalAmountTendered: Double = .0,
    @SerializedName("change_required")
    var changeRequired: Double = .0,
    @SerializedName("created_at")
    var createdAt: Date = Date(),
    @SerializedName("deleted_at")
    var deletedAt: Date? = null,
    @SerializedName("updated_at")
    var updatedAt: Date = Date(),
    @SerializedName("order_items")
    var orderItems: List<OrderItemRequest> = emptyList(),
    var payments: List<OrderPaymentRequest> = emptyList(),
    var table: OrderTableRequest? = null,
    @SerializedName("waiter_id")
    var waiterId: String = "",
    @SerializedName("order_status_changes")
    var orderStatusChanges: List<OrderStatusChangeRequest> = emptyList(),
)

data class OrderStatusChangeRequest(
    var id: String = "",
    @SerializedName("order_id")
    var orderId: String = "",
    var status: Int = 0,
    @SerializedName("changed_by")
    var changedBy: String = "",
    @SerializedName("created_at")
    var createdAt: Date = Date(),
    @SerializedName("deleted_at")
    var deletedAt: Date? = null,
    @SerializedName("updated_at")
    var updatedAt: Date = Date(),
)

data class OrderItemRequest(
    var id: String = "",
    @SerializedName("order_id")
    var orderId: String = "",
    @SerializedName("product_id")
    var productId: String = "",
    var name: String = "",
    var quantity: Double = .0,
    @SerializedName("is_takeaway")
    var isTakeaway: Boolean = false,
    @SerializedName("price_excluding_tax")
    var priceExcludingTax: Double = .0,
    var tax: Double = .0,
    @SerializedName("is_tax_inclusive")
    var isTaxInclusive: Boolean = false,
    var cost: Double = .0,
    @SerializedName("preparing_duration")
    var preparingDuration: Int = 0,
    var status: Int = 0,
    @SerializedName("item_note")
    var itemNote: String = "",
    @SerializedName("created_at")
    var createdAt: Date = Date(),
    @SerializedName("deleted_at")
    var deletedAt: Date? = null,
    @SerializedName("updated_at")
    var updatedAt: Date = Date(),
    @SerializedName("item_status_changes")
    var itemStatusChanges: List<ItemStatusChangeRequest> = emptyList(),
    @SerializedName("staffs")
    var staffs: List<ItemStaffRequest> = emptyList(),
)

data class ItemStatusChangeRequest(
    var id: String = "",
    @SerializedName("item_id")
    var itemId: String = "",
    var status: Int = 0,
    @SerializedName("changed_by")
    var changedBy: String = "",
    @SerializedName("created_at")
    var createdAt: Date = Date(),
    @SerializedName("deleted_at")
    var deletedAt: Date? = null,
    @SerializedName("updated_at")
    var updatedAt: Date = Date(),
)

data class ItemStaffRequest(
    var id: String = "",
    @SerializedName("item_id")
    var itemId: String = "",
    @SerializedName("staff_id")
    var staffId: String = "",
    @SerializedName("created_at")
    var createdAt: Date = Date(),
    @SerializedName("deleted_at")
    var deletedAt: Date? = null,
    @SerializedName("updated_at")
    var updatedAt: Date = Date(),
)

data class OrderPaymentRequest(
    var id: String = "",
    @SerializedName("order_id")
    var orderId: String = "",
    @SerializedName("payment_method_id")
    var paymentMethodId: String = "",
    var amount: Double = .0,
    @SerializedName("reference_number")
    var referenceNumber: String = "",
    @SerializedName("created_at")
    var createdAt: Date = Date(),
    @SerializedName("deleted_at")
    var deletedAt: Date? = null,
    @SerializedName("updated_at")
    var updatedAt: Date = Date(),
)

data class OrderTableRequest(
    var id: String = "",
    @SerializedName("order_id")
    var orderId: String = "",
    @SerializedName("table_id")
    var tableId: String = "",
    @SerializedName("created_at")
    var createdAt: Date = Date(),
    @SerializedName("deleted_at")
    var deletedAt: Date? = null,
    @SerializedName("updated_at")
    var updatedAt: Date = Date(),
)
