package com.desapabandara.pos.base.manager

import co.mbznetwork.android.base.di.DeviceID
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import co.mbznetwork.android.base.util.DateUtil
import com.desapabandara.pos.base.R
import com.desapabandara.pos.base.eventbus.OrderPaymentEventBus
import com.desapabandara.pos.base.eventbus.OrderPrintEventBus
import com.desapabandara.pos.base.eventbus.StaffLoginEventBus
import com.desapabandara.pos.base.model.ItemInfo
import com.desapabandara.pos.base.model.ItemStaff
import com.desapabandara.pos.base.model.ItemStatus
import com.desapabandara.pos.base.model.OrderStatus
import com.desapabandara.pos.base.model.OrderType
import com.desapabandara.pos.base.model.PaymentStatus
import com.desapabandara.pos.local_db.dao.OrderDao
import com.desapabandara.pos.local_db.dao.OrderItemDao
import com.desapabandara.pos.local_db.dao.OrderPaymentDao
import com.desapabandara.pos.local_db.dao.OrderTableDao
import com.desapabandara.pos.local_db.dao.PaymentMethodDao
import com.desapabandara.pos.local_db.dao.ProductDao
import com.desapabandara.pos.local_db.dao.TableDao
import com.desapabandara.pos.local_db.entity.OrderEntity
import com.desapabandara.pos.local_db.entity.OrderItemEntity
import com.desapabandara.pos.local_db.entity.OrderPaymentEntity
import com.desapabandara.pos.local_db.entity.OrderTableEntity
import com.desapabandara.pos.base.model.Order
import com.desapabandara.pos.base.model.OrderItem
import com.desapabandara.pos.base.model.OrderPayment
import com.desapabandara.pos.base.model.OrderPaymentEvent
import com.desapabandara.pos.base.model.OrderPrintJob
import com.desapabandara.pos.base.model.OrderStaff
import com.desapabandara.pos.base.model.OrderTable
import com.desapabandara.pos.base.model.OrderWaiter
import com.desapabandara.pos.base.model.PaymentMethod
import com.desapabandara.pos.base.model.PaymentMethodType
import com.desapabandara.pos.base.model.Table
import com.desapabandara.pos.local_db.dao.ItemStaffDao
import com.desapabandara.pos.local_db.dao.ItemStatusChangesDao
import com.desapabandara.pos.local_db.dao.OrderStatusChangesDao
import com.desapabandara.pos.local_db.dao.StaffDao
import com.desapabandara.pos.local_db.dao.StaffLocationAssignmentDao
import com.desapabandara.pos.local_db.entity.ItemStaffEntity
import com.desapabandara.pos.local_db.entity.ItemStatusChangesEntity
import com.desapabandara.pos.local_db.entity.OrderStatusChangesEntity
import com.desapabandara.pos.preference.datastore.AuthDataStore
import com.desapabandara.pos.preference.datastore.OrderDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderManager @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val orderPaymentDao: OrderPaymentDao,
    private val orderTableDao: OrderTableDao,
    private val tableDao: TableDao,
    private val orderDataStore: OrderDataStore,
    private val authDataStore: AuthDataStore,
    private val productDao: ProductDao,
    private val paymentMethodDao: PaymentMethodDao,
    private val uiStatusEventBus: UIStatusEventBus,
    private val staffDao: StaffDao,
    private val staffLocationAssignmentDao: StaffLocationAssignmentDao,
    private val itemStaffDao: ItemStaffDao,
    private val itemStatusChangesDao: ItemStatusChangesDao,
    private val orderStatusChangesDao: OrderStatusChangesDao,
    private val orderPrintEventBus: OrderPrintEventBus,
    private val staffLoginEventBus: StaffLoginEventBus,
    private val orderPaymentEventBus: OrderPaymentEventBus,
    @DeviceID private val deviceId: String
) {
    private var scope: CoroutineScope? = null

    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder = _currentOrder.asStateFlow()

    fun start() {
        scope?.cancel()
        scope = CoroutineScope(ioDispatcher + SupervisorJob()).also {
            it.launch {
                mapOrderData(orderDao.getActiveOrder()).flowOn(ioDispatcher).collect {
                    _currentOrder.value = mapOrderFlowToDisplay(it)
                }
            }
        }

        monitorOrderPayments()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun mapOrderData(orderData: Flow<OrderEntity?>) = orderData.flatMapLatest { order ->
        if (order != null) {
            combine(
                orderItemDao.getOrderItemsByOrderId(order.id),
                orderPaymentDao.getOrderPaymentsByOrderId(order.id),
                orderTableDao.getOrderTableByOrderId(order.id).map {
                    if (it != null) {
                        tableDao.getTable(it.tableId)?.let { table ->
                            return@map it.id to table
                        }
                    }

                    null to null
                }
            ) { items, payments, (orderTableId, table) ->
                OrderFlowMap(order, items, payments, orderTableId?.let { otId ->
                    table?.let { t ->
                        OrderTable(otId, Table(
                            t.id,
                            t.name
                        )
                        )
                    }
                })
            }
        } else {
            flow<OrderFlowMap?> { emit(null) }
        }
    }

    private suspend fun mapOrderFlowToDisplay(orderFlowMap: OrderFlowMap?) = orderFlowMap?.let {
        with(it.order) {
            Order(
                id,
                storeId,
                invoiceNumber,
                orderNumber,
                adultMaleCount,
                adultFemaleCount,
                childMaleCount,
                childFemaleCount,
                adultMaleCount + adultFemaleCount + childMaleCount + childFemaleCount,
                OrderType.fromId(orderType) ?: OrderType.EatIn,
                subtotalExcludingTax,
                subtotalTax,
                subtotalExcludingTax + subtotalTax,
                discountExcludingTax,
                discountTax,
                discountExcludingTax + discountTax,
                surchargeExcludingTax,
                surchargeTax,
                surchargeExcludingTax + surchargeTax,
                totalExcludingTax,
                totalTax,
                totalExcludingTax + totalTax,
                totalCost,
                OrderStatus.fromId(orderStatus) ?: OrderStatus.Active,
                PaymentStatus.fromId(paymentStatus) ?: PaymentStatus.Open,
                expectedTotalPreparingDuration,
                createdBy,
                customerName,
                orderNote,
                Date(createdAt),
                deletedAt?.let { d -> Date(d) },
                Date(updatedAt),
                synced,
                it.orderItems.map { item ->
                    val staffs = itemStaffDao.getStaffsFromItem(item.id)
                    with(item) {
                        OrderItem(
                            id,
                            productId,
                            name,
                            quantity,
                            isTakeaway,
                            priceExcludingTax,
                            tax,
                            priceExcludingTax + tax,
                            (priceExcludingTax + tax) * quantity,
                            isTaxInclusive,
                            cost,
                            preparingDuration,
                            ItemStatus.fromId(status) ?: ItemStatus.New,
                            itemNote,
                            Date(createdAt),
                            deletedAt?.let { d -> Date(d) },
                            Date(updatedAt),
                            staffs.map { s -> ItemStaff(s.id, s.staffId) }
                        )
                    }
                },
                it.orderPayments.map { payment ->
                    with(payment) {
                        OrderPayment(
                            id,
                            paymentMethodId,
                            amount,
                            referenceNumber,
                            Date(createdAt),
                            deletedAt?.let { d -> Date(d) },
                            Date(updatedAt),
                            paymentMethodDao.getPaymentMethod(paymentMethodId)?.run {
                                PaymentMethod(
                                    id,
                                    name,
                                    PaymentMethodType.fromId(paymentMethodType)
                                )
                            } ?: PaymentMethod(
                                "0",
                                "Unknown",
                                PaymentMethodType.Other
                            )
                        )
                    }
                },
                it.orderTable,
                staffDao.getStaffByIdSingle(createdBy)?.run {
                    OrderStaff(id, name)
                },
                null,
                waiterId,
                if (waiterId.isNotBlank() && waiterId != "0") {
                    staffDao.getStaffByIdSingle(waiterId)?.run {
                        OrderWaiter(id, name)
                    }
                } else null,
                isNewCustomer
            )
        }
    }

    suspend fun getOrderDataDisplayFlow(id: String) = mapOrderData(orderDao.getOrder(id)).map {
        mapOrderFlowToDisplay(it)
    }

    private suspend fun generateOrderNumberAndInvoice(): OrderNumberAndInvoice {
        val currentDate = DateUtil.format(Date(), "yyyyMMdd")
        val lastOrderDate = orderDataStore.getOrderStartDate().first()
        val lastOrderNumber = if (currentDate == lastOrderDate) {
            orderDataStore.getLastOrderNumber().first()
        } else {
            0
        }

        val currentStore = authDataStore.getCurrentStoreData().first()
        val currentStoreId = currentStore?.id ?: "0"

        val orderNumber = lastOrderNumber+1
        val devicePrefix = deviceId.substring(0, 5)
        val invoice = "INV/$currentStoreId/$devicePrefix/$currentDate/$orderNumber"

        return OrderNumberAndInvoice(
            orderNumber,
            invoice,
            currentStore?.id ?: "0"
        ).also {
            orderDataStore.run {
                storeLastOrderNumber(orderNumber)
                if (currentDate != lastOrderDate) {
                    storeOrderStartDate(currentDate)
                }
            }
        }
    }

    private suspend fun createEmptyOrder(): String {
        val currentStaff = staffLoginEventBus.currentStaff.value

        val orderId = UUID.randomUUID().toString()
        generateOrderNumberAndInvoice().run {
            orderDao.save(OrderEntity(
                orderId,
                storeId,
                invoice,
                orderNumber,
                0,
                0,
                0,
                0,
                OrderType.EatIn.id,
                .0,
                .0,
                .0,
                .0,
                .0,
                .0,
                .0,
                .0,
                .0,
                OrderStatus.Active.id,
                PaymentStatus.Open.id,
                0,
                currentStaff?.id ?: "0",
                "",
                "",
                "",
                synced = false,
            ))

            addOrderStatusChange(orderId, OrderStatus.Active)
        }

        return orderId
    }

    private suspend fun calculateOrder(orderId: String) {
        val order = orderDao.getOrderById(orderId) ?: return

        val orderItems = orderItemDao.getOrderItemsByOrder(orderId)

        var subtotalExcludingTax = .0
        var subtotalTax = .0
        var expectedPreparingDuration = 0
        var discountExcludingTax = .0
        var discountTax = .0
        var surchargeExcludingTax = .0
        var surchargeTax = .0

        orderItems.forEach {
            subtotalExcludingTax += (it.priceExcludingTax * it.quantity)
            subtotalTax += (it.tax * it.quantity)
            expectedPreparingDuration += (it.preparingDuration * it.quantity.toInt())
        }

        val totalExcludingTax = subtotalExcludingTax - discountExcludingTax + surchargeExcludingTax
        val totalTax = subtotalTax - discountTax + surchargeTax

        orderDao.update(order.apply {
            this.subtotalExcludingTax = subtotalExcludingTax
            this.subtotalTax = subtotalTax
            this.discountExcludingTax = discountExcludingTax
            this.discountTax = discountTax
            this.surchargeExcludingTax = surchargeExcludingTax
            this.surchargeTax = surchargeTax
            this.totalExcludingTax = totalExcludingTax
            this.totalTax = totalTax
            this.expectedTotalPreparingDuration = expectedPreparingDuration
        })
    }

    private suspend fun assignStaffsToItem(itemId: String, locationId: String) {
        val staffs = staffLocationAssignmentDao.getStaffsAssignedByLocation(locationId)

        if (staffs.isEmpty()) return

        for (staff in staffs) {
            itemStaffDao.save(
                ItemStaffEntity(
                    UUID.randomUUID().toString(),
                    itemId,
                    staff.staffId
                )
            )
        }
    }

    fun addOrderItem(productId: String) {
        scope?.launch {
            val orderId = currentOrder.value?.id ?: createEmptyOrder()

            val product = productDao.getProductById(productId)
            if (product == null) {
                uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                    UiMessage.ResourceMessage(R.string.product_is_not_exist)
                ))

                return@launch
            }

            val existingItem = orderItemDao.getOrderItemByProductIdAndOrderId(orderId, productId, ItemStatus.New.id)

            if (existingItem == null) {
                val itemId = UUID.randomUUID().toString()
                orderItemDao.save(
                    OrderItemEntity(
                        itemId,
                        orderId,
                        product.id,
                        product.name,
                        1.0,
                        false,
                        product.priceExcludingTax,
                        product.tax,
                        product.isTaxInclusive,
                        product.cost,
                        product.preparingDuration,
                        ItemStatus.New.id,
                        ""
                    )
                )

                addItemStatusChange(itemId, ItemStatus.New)
                assignStaffsToItem(itemId, product.locationId)
            } else {
                existingItem.apply {
                    quantity += 1.0
                    name = product.name
                    priceExcludingTax = product.priceExcludingTax
                    tax = product.tax
                    isTaxInclusive = product.isTaxInclusive
                    cost = product.cost
                    preparingDuration = product.preparingDuration
                }

                orderItemDao.update(existingItem)
            }

            calculateOrder(orderId)
        }
    }

    fun removeOrderItem(itemId: String) {
        scope?.launch {
            val orderItem = orderItemDao.getOrderItemById(itemId) ?: return@launch

            itemStaffDao.deleteStaffsFromItem(itemId)
            itemStatusChangesDao.deleteChangesFromItem(itemId)
            orderItemDao.delete(orderItem)

            calculateOrder(orderItem.orderId)
        }
    }

    fun clearCurrentOrder() {
        scope?.launch {
            currentOrder.value?.let {
                val items = orderItemDao.getOrderItemsByOrder(it.id)
                items.forEach { item ->
                    itemStaffDao.deleteStaffsFromItem(item.id)
                    itemStatusChangesDao.deleteChangesFromItem(item.id)
                }
                orderItemDao.deleteItemsInOrder(it.id)
                orderTableDao.deleteTableFromOrder(it.id)
                orderStatusChangesDao.deleteChangesFromOrder(it.id)
                orderDao.deleteOrder(it.id)

                var lastOrderNumber = orderDataStore.getLastOrderNumber().first()
                if (lastOrderNumber != it.orderNumber) return@launch

                lastOrderNumber = (it.orderNumber - 1).let { n ->
                    if (n < 0) 0 else n
                }
                orderDataStore.storeLastOrderNumber(lastOrderNumber)
            }
        }
    }

    fun setTable(tableId: String) {
        scope?.launch {
            currentOrder.value?.let {
                if (tableId.isNotBlank()) {
                    val orderTable = orderTableDao.getOrderTableByOrder(it.id)?.apply {
                        this.tableId = tableId
                    } ?: OrderTableEntity(
                        UUID.randomUUID().toString(),
                        it.id,
                        tableId
                    )
                    orderTableDao.save(orderTable)
                } else {
                    orderTableDao.getOrderTableByOrder(it.id)?.apply {
                        orderTableDao.delete(this)
                    }
                }

            }
        }
    }

    fun setOrderType(type: OrderType) {
        scope?.launch {
            currentOrder.value?.let {
                orderDao.getOrderById(it.id)?.apply {
                    this.orderType = type.id

                    orderDao.update(this)
                }
            }
        }
    }

    fun sendOrder() {
        scope?.launch {
            currentOrder.value?.let {
                if (it.waiterId.isBlank() || it.waiterId == "0") {
                    uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                        UiMessage.ResourceMessage(R.string.select_waiter_error)
                    ))
                    return@launch
                }

                orderDao.getOrderById(it.id)?.apply {
                    val updateTime = System.currentTimeMillis()
                    this.orderStatus = OrderStatus.Sent.id
                    createdAt = updateTime

                    orderDao.update(this)
                    addOrderStatusChange(it.id, OrderStatus.Sent)

                    orderPrintEventBus.publishJob(OrderPrintJob(
                        it.apply {
                            this.createdAt = Date(updateTime)
                        },
                    ))

                    markItemsSentAndPrinted(it.id)
                }
            }
        }
    }

    fun activateOrder(id: String) {
        scope?.launch {
            val currentActiveOrder = currentOrder.value
            if (currentActiveOrder != null) {
                uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                    UiMessage.ResourceMessage(R.string.activate_order_failed)
                ))

                return@launch
            }

            orderDao.getOrderById(id)?.apply {
                if (orderStatus > OrderStatus.Prepared.id) {
                    uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                        UiMessage.ResourceMessage(R.string.order_completed_error)
                    ))
                    return@apply
                }

                this.orderStatus = OrderStatus.Active.id

                orderDao.update(this)
                addOrderStatusChange(id, OrderStatus.Active)
            }
        }
    }

    private fun isOrderHasBeenSent() =
        currentOrder.value?.orderItems?.any { it.status == ItemStatus.Sent } ?: false

    fun canPayOrder(): Boolean {
        return currentOrder.value?.let {
            if (it.waiterId.isBlank() || it.waiterId == "0") {
                uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                    UiMessage.ResourceMessage(R.string.select_waiter_error)
                ))
                return@let false
            }

            true
        } ?: false
    }

    private fun processPayment(payment: OrderPaymentEvent.Settled) {
        scope?.launch {
            currentOrder.value?.let {
                if (it.id != payment.orderId) return@launch

                orderDao.getOrderById(payment.orderId)?.apply order@ {
                    val defaultPaymentMethod = paymentMethodDao.getFirstPaymentMethod()
                    val orderHasBeenSent = isOrderHasBeenSent()

                    orderPaymentDao.save(
                        OrderPaymentEntity(
                            UUID.randomUUID().toString(),
                            id,
                            payment.paymentMethod.id,
                            payment.amount,
                            payment.referenceNumber
                        )
                    )

                    if (it.total <= payment.amount) {
                        orderStatus = OrderStatus.Completed.id
                        paymentStatus = PaymentStatus.Settled.id
                        totalAmountTendered = orderPaymentDao.getTotalPaidAmountForOrder(payment.orderId)
                        changeRequired = totalAmountTendered - it.total

                        if (!orderHasBeenSent) {
                            createdAt = System.currentTimeMillis()
                        }
                        orderDao.update(this)
                        addOrderStatusChange(it.id, OrderStatus.Completed)

                        orderPrintEventBus.publishJob(OrderPrintJob(
                            it.apply {
                                orderStatus = OrderStatus.Completed
                                paymentStatus = PaymentStatus.Settled
                                this.createdAt = Date(this@order.createdAt)
                                orderPayments = orderPaymentDao.getOrderPaymentsByOrder(it.id).map { op ->
                                    with(op) {
                                        OrderPayment(
                                            id,
                                            paymentMethodId,
                                            amount,
                                            referenceNumber,
                                            Date(createdAt),
                                            deletedAt?.let { d -> Date(d) },
                                            Date(updatedAt),
                                            paymentMethodDao.getPaymentMethod(paymentMethodId)
                                                ?.run {
                                                    PaymentMethod(
                                                        id,
                                                        name,
                                                        PaymentMethodType.fromId(paymentMethodType)
                                                    )
                                                } ?: PaymentMethod(
                                                "0",
                                                "Unknown",
                                                PaymentMethodType.Other
                                            )
                                        )
                                    }
                                }
                            }
                        ))

                        markItemsSentAndPrinted(it.id)
                    }
                }
            }
        }
    }

    private fun monitorOrderPayments() {
        scope?.launch {
            orderPaymentEventBus.orderPaymentEvent.collect {
                val currentOrderId = currentOrder.value?.id ?: return@collect

                when (it) {
                    is OrderPaymentEvent.Settled -> {
                        processPayment(it)
                    }
                    is OrderPaymentEvent.Failed -> {
                        if (it.orderId != currentOrderId) return@collect

                        uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                            UiMessage.StringMessage(it.errorMessage)
                        ))
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun addItemStatusChange(itemId: String, status: ItemStatus) {
        itemStatusChangesDao.save(
            ItemStatusChangesEntity(
                id = UUID.randomUUID().toString(),
                itemId = itemId,
                status = status.id,
                changedBy = staffLoginEventBus.currentStaff.value?.id ?: "0"
            )
        )
    }

    fun updateOrderItemStatus(orderId: String, itemId: String, status: ItemStatus) {
        scope?.launch {
            val orderItem = orderItemDao.getOrderItemById(itemId) ?: return@launch

            orderItemDao.update(orderItem.apply {
                this.status = status.id
            })

            addItemStatusChange(itemId, status)

            orderDao.setUpdatedAt(orderId)
        }
    }

    private fun markItemsSentAndPrinted(orderId: String) {
        scope?.launch {
            val items = orderItemDao.getOrderItemsByOrder(orderId).filter {
                it.status == ItemStatus.New.id
            }
            items.forEach {
                updateOrderItemStatus(orderId, it.id, ItemStatus.Sent)
            }
        }
    }

    private suspend fun addOrderStatusChange(orderId: String, status: OrderStatus) {
        orderStatusChangesDao.save(
            OrderStatusChangesEntity(
                id = UUID.randomUUID().toString(),
                orderId = orderId,
                status = status.id,
                changedBy = staffLoginEventBus.currentStaff.value?.id ?: "0"
            )
        )
    }

    fun addCustomer(
        id: String,
        name: String,
        phoneNumber: String,
        adultMaleCount: Int,
        adultFemaleCount: Int,
        childMaleCount: Int,
        childFemaleCount: Int,
        isNewCustomer: Boolean
    ) {
        scope?.launch {
            currentOrder.value?.let {
                val order = orderDao.getOrderById(it.id) ?: return@let

                orderDao.update(order.apply {
                    customerName = name
                    this.adultMaleCount = adultMaleCount
                    this.adultFemaleCount = adultFemaleCount
                    this.childMaleCount = childMaleCount
                    this.childFemaleCount = childFemaleCount
                    this.isNewCustomer = isNewCustomer
                })
            }
        }
    }

    fun addItemInfo(info: ItemInfo) {
        scope?.launch {
            val orderItem = orderItemDao.getOrderItemById(info.id) ?: return@launch

            orderItemDao.update(orderItem.apply {
                quantity = info.quantity
                itemNote = info.note
                isTakeaway = info.isTakeaway
            })

            calculateOrder(orderItem.orderId)
        }
    }

    fun selectWaiter(staffId: String) {
        scope?.launch {
            currentOrder.value?.let {
                val order = orderDao.getOrderById(it.id) ?: return@let

                orderDao.update(order.apply {
                    waiterId = staffId
                })
            }
        }
    }

    fun stop() {
        scope?.cancel()
        scope = null
    }

}

data class OrderFlowMap(
    val order: OrderEntity,
    val orderItems: List<OrderItemEntity>,
    val orderPayments: List<OrderPaymentEntity>,
    val orderTable: OrderTable?
)

data class OrderNumberAndInvoice(
    val orderNumber: Int,
    val invoice: String,
    val storeId: String,
)
