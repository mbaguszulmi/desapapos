package com.desapabandara.pos.base.manager

import co.mbznetwork.android.base.di.DeviceID
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import co.mbznetwork.android.base.util.DateUtil
import com.desapabandara.pos.base.R
import com.desapabandara.pos.base.eventbus.OrderPrintEventBus
import com.desapabandara.pos.base.model.ItemInfo
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
import com.desapabandara.pos.base.model.OrderPrintJob
import com.desapabandara.pos.base.model.OrderStaff
import com.desapabandara.pos.base.model.OrderTable
import com.desapabandara.pos.base.model.Table
import com.desapabandara.pos.local_db.dao.StaffDao
import com.desapabandara.pos.preference.datastore.AuthDataStore
import com.desapabandara.pos.preference.datastore.OrderDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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
    private val authManager: AuthManager,
    private val productDao: ProductDao,
    private val paymentMethodDao: PaymentMethodDao,
    private val uiStatusEventBus: UIStatusEventBus,
    private val staffDao: StaffDao,
    private val orderPrintEventBus: OrderPrintEventBus,
    @DeviceID private val deviceId: String
) {
    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())

    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder = _currentOrder.asStateFlow()

    fun start() {
        scope.launch {
            mapOrderData(orderDao.getActiveOrder()).flowOn(ioDispatcher).collect {
                _currentOrder.value = mapOrderFlowToDisplay(it)
            }
        }
    }

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
                com.desapabandara.pos.base.model.OrderType.fromId(orderType) ?: com.desapabandara.pos.base.model.OrderType.EatIn,
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
                com.desapabandara.pos.base.model.OrderStatus.fromId(orderStatus) ?: com.desapabandara.pos.base.model.OrderStatus.Active,
                com.desapabandara.pos.base.model.PaymentStatus.fromId(paymentStatus) ?: com.desapabandara.pos.base.model.PaymentStatus.Open,
                expectedTotalPreparingDuration,
                createdBy,
                customerName,
                orderNote,
                Date(createdAt),
                deletedAt?.let { d -> Date(d) },
                Date(updatedAt),
                synced,
                it.orderItems.map { item ->
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
                            com.desapabandara.pos.base.model.ItemStatus.fromId(status) ?: com.desapabandara.pos.base.model.ItemStatus.New,
                            itemNote,
                            Date(createdAt),
                            deletedAt?.let { d -> Date(d) },
                            Date(updatedAt),
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
                        )
                    }
                },
                it.orderTable,
                staffDao.getStaffByIdSingle(createdBy)?.run {
                    OrderStaff(id, name)
                },
                null
            )
        }
    }

    suspend fun getOrderDataDisplayFlow(id: String) = mapOrderData(orderDao.getOrder(id)).map {
        mapOrderFlowToDisplay(it)
    }

    private suspend fun generateOrderNumberAndInvoice(): OrderNumberAndInvoice {
        val currentDate = DateUtil.format(Date(), "yyyyMMdd")
        val lastOrderNumber = orderDataStore.getLastOrderNumber().first()
        val lastOrderDate = orderDataStore.getOrderStartDate().first()
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
        val currentStaff = authManager.currentStaff.value

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
                synced = false,
            ))
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

    fun addOrderItem(productId: String) {
        scope.launch {
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
                orderItemDao.save(
                    OrderItemEntity(
                        UUID.randomUUID().toString(),
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
        scope.launch {
            val orderItem = orderItemDao.getOrderItemById(itemId) ?: return@launch

            orderItemDao.delete(orderItem)

            calculateOrder(orderItem.orderId)
        }
    }

    fun clearCurrentOrder() {
        scope.launch {
            currentOrder.value?.let {
                orderItemDao.deleteItemsInOrder(it.id)
                orderTableDao.deleteTableFromOrder(it.id)
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
        scope.launch {
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
        scope.launch {
            currentOrder.value?.let {
                orderDao.getOrderById(it.id)?.apply {
                    this.orderType = type.id

                    orderDao.update(this)
                }
            }
        }
    }

    fun sendOrder() {
        scope.launch {
            currentOrder.value?.let {
                orderDao.getOrderById(it.id)?.apply {
                    val updateTime = System.currentTimeMillis()
                    this.orderStatus = OrderStatus.Sent.id
                    createdAt = updateTime

                    orderDao.update(this)
                    orderPrintEventBus.publishJob(OrderPrintJob(
                        it.apply {
                            this.createdAt = Date(updateTime)
                        },
                    ))
                }
            }
        }
    }

    fun activateOrder(id: String) {
        scope.launch {
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
            }
        }
    }

    private fun isOrderHasBeenSent() =
        currentOrder.value?.orderItems?.any { it.status == ItemStatus.Sent } ?: false

    fun payOrder() {
        scope.launch {
            currentOrder.value?.let {
                orderDao.getOrderById(it.id)?.apply order@ {
                    val defaultPaymentMethod = paymentMethodDao.getFirstPaymentMethod()

                    orderPaymentDao.save(
                        OrderPaymentEntity(
                            UUID.randomUUID().toString(),
                            it.id,
                            defaultPaymentMethod?.id ?: "1",
                            it.total,
                            UUID.randomUUID().toString()
                        )
                    )

                    orderStatus = OrderStatus.Completed.id
                    paymentStatus = PaymentStatus.Settled.id

                    if (!isOrderHasBeenSent()) {
                        createdAt = System.currentTimeMillis()
                    }

                    orderDao.update(this)

                    orderPrintEventBus.publishJob(OrderPrintJob(
                        it.apply {
                            orderStatus = OrderStatus.Completed
                            this.createdAt = Date(this@order.createdAt)
                        }
                    ))
                }
            }
        }
    }

    fun markItemsSentAndPrinted(id: String, itemIds: List<String>) {
        scope.launch {
            val items = if (itemIds.isEmpty()) orderItemDao.getOrderItemsByOrder(id) else orderItemDao.getOrderItems(itemIds)
            items.forEach {
                orderItemDao.update(it.apply {
                    status = ItemStatus.Sent.id
                })
            }
        }
    }

    fun addCustomer(
        id: String,
        name: String,
        phoneNumber: String,
        adultMaleCount: Int,
        adultFemaleCount: Int,
        childMaleCount: Int,
        childFemaleCount: Int
    ) {
        scope.launch {
            currentOrder.value?.let {
                val order = orderDao.getOrderById(it.id) ?: return@let

                orderDao.update(order.apply {
                    customerName = name
                    this.adultMaleCount = adultMaleCount
                    this.adultFemaleCount = adultFemaleCount
                    this.childMaleCount = childMaleCount
                    this.childFemaleCount = childFemaleCount
                })
            }
        }
    }

    fun addItemInfo(info: ItemInfo) {
        scope.launch {
            val orderItem = orderItemDao.getOrderItemById(info.id) ?: return@launch

            orderItemDao.update(orderItem.apply {
                quantity = info.quantity
                itemNote = info.note
                isTakeaway = info.isTakeaway
            })

            calculateOrder(orderItem.orderId)
        }
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
