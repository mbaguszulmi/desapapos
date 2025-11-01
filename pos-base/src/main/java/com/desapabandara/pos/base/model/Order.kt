package com.desapabandara.pos.base.model

import java.util.Date

data class Order(
    var id: String,
    var storeId: String,
    var invoiceNumber: String,
    var orderNumber: Int,
    var adultMaleCount: Int,
    var adultFemaleCount: Int,
    var childMaleCount: Int,
    var childFemaleCount: Int,
    var totalCustomerCount: Int,
    var orderType: OrderType,
    var subtotalExcludingTax: Double,
    var subtotalTax: Double,
    var subtotal: Double,
    var discountExcludingTax: Double,
    var discountTax: Double,
    var discount: Double,
    var surchargeExcludingTax: Double,
    var surchargeTax: Double,
    var surcharge: Double,
    var totalExcludingTax: Double,
    var totalTax: Double,
    var total: Double,
    var totalCost: Double,
    var orderStatus: OrderStatus,
    var paymentStatus: PaymentStatus,
    var expectedTotalPreparingDuration: Int,
    var createdBy: String,
    var customerName: String,
    var orderNote: String,
    var createdAt: Date,
    var deletedAt: Date? = null,
    var updatedAt: Date,
    var synced: Boolean = false,
    var orderItems: List<OrderItem>,
    var orderPayments: List<OrderPayment>,
    var orderTable: OrderTable?,
    var staff: OrderStaff?,
    var customer: OrderCustomer?,
    var waiterId: String,
    var waiter: OrderWaiter?,
)

data class OrderWaiter(
    var id: String,
    var name: String,
)

data class OrderItem(
    var id: String,
    var productId: String,
    var name: String,
    var quantity: Double,
    var isTakeaway: Boolean,
    var priceExcludingTax: Double,
    var tax: Double,
    var price: Double,
    var totalPrice: Double,
    var isTaxInclusive: Boolean,
    var cost: Double,
    var preparingDuration: Int,
    var status: ItemStatus,
    var itemNote: String,
    var createdAt: Date,
    var deletedAt: Date? = null,
    var updatedAt: Date,
    var staffs: List<ItemStaff>
)

data class ItemStaff(
    var id: String,
    var staffId: String,
)

data class OrderPayment(
    var id: String,
    var paymentMethodId: String,
    var amount: Double,
    var referenceNumber: String,
    var createdAt: Date,
    var deletedAt: Date? = null,
    var updatedAt: Date,
)

data class OrderTable(
    var id: String,
    var table: Table,
)

data class Table(
    var id: String,
    var name: String
)

data class OrderStaff(
    var id: String,
    var name: String,
)

data class OrderCustomer(
    var id: String,
    var customer: Customer,
)

data class Customer(
    var id: String,
    var name: String,
    var phone: String,
)
