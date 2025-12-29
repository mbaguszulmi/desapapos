package com.desapabandara.pos.sync.manager

import co.mbznetwork.android.base.NetworkStatusManager
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.util.DateUtil
import co.mbznetwork.android.base.util.handleOnlineData
import com.desapabandara.pos.base.repository.OnlineRepository
import com.desapabandara.pos.local_db.dao.ItemStaffDao
import com.desapabandara.pos.local_db.dao.ItemStatusChangesDao
import com.desapabandara.pos.local_db.dao.OrderDao
import com.desapabandara.pos.local_db.dao.OrderItemDao
import com.desapabandara.pos.local_db.dao.OrderPaymentDao
import com.desapabandara.pos.local_db.dao.OrderStatusChangesDao
import com.desapabandara.pos.local_db.dao.OrderTableDao
import com.desapabandara.pos.preference.datastore.OrderDataStore
import com.desapabandara.pos_backend.model.request.ItemStaffRequest
import com.desapabandara.pos_backend.model.request.ItemStatusChangeRequest
import com.desapabandara.pos_backend.model.request.OrderItemRequest
import com.desapabandara.pos_backend.model.request.OrderPaymentRequest
import com.desapabandara.pos_backend.model.request.OrderRequest
import com.desapabandara.pos_backend.model.request.OrderStatusChangeRequest
import com.desapabandara.pos_backend.model.request.OrderTableRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderSyncManager @Inject constructor(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val orderPaymentDao: OrderPaymentDao,
    private val orderTableDao: OrderTableDao,
    private val orderDataStore: OrderDataStore,
    private val onlineRepository: OnlineRepository,
    private val networkStatusManager: NetworkStatusManager,
    private val orderStatusChangesDao: OrderStatusChangesDao,
    private val itemStatusChangesDao: ItemStatusChangesDao,
    private val itemStaffDao: ItemStaffDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var scope: CoroutineScope? = null
    private var syncJob: Job? = null

    fun start() {
        scope?.cancel()
        scope = CoroutineScope(ioDispatcher + SupervisorJob()).also {
            it.launch {
                Timber.d("Starting Order Sync Manager")
                networkStatusManager.hasInternet.collect { hasInternet ->
                    if (hasInternet) {
                        startSyncing()
                    } else {
                        stopSyncing()
                    }
                }
            }
        }
    }

    private fun startSyncing() {
        syncJob?.cancel()
        syncJob = scope?.launch {
            Timber.d("Order Sync started")
            while (true) {
                val lastOrderSync = orderDataStore.getLastOrderSync().first()
                Timber.d("Last order synced ${DateUtil.formatDateTimeShort(Date(lastOrderSync ?: 0L))}")
                val orders = if (lastOrderSync == null) {
                    orderDao.getUnSyncedOrder().filterNot { it.isEmpty() }.first()
                } else {
                    orderDao.getUnSyncedOrder(lastOrderSync).filterNot { it.isEmpty() }.first()
                }

                Timber.d("Got ${orders.size} order(s) to be synced")

                val currentTimestamp = System.currentTimeMillis()

                orders.forEach {
                    Timber.d("Syncing order: ${it.id}")
                    val orderItems = orderItemDao.getOrderItemsByOrder(it.id)
                    val orderPayments = orderPaymentDao.getOrderPaymentsByOrder(it.id)
                    val orderTable = orderTableDao.getOrderTableByOrder(it.id)
                    val orderStatusChanges = orderStatusChangesDao.getOrderStatusChangesByOrder(it.id)

                    handleOnlineData(
                        with(it) {
                            OrderRequest(
                                id,
                                storeId,
                                invoiceNumber,
                                orderNumber,
                                adultMaleCount,
                                adultFemaleCount,
                                childMaleCount,
                                childFemaleCount,
                                orderType,
                                subtotalExcludingTax,
                                subtotalTax,
                                discountExcludingTax,
                                discountTax,
                                surchargeExcludingTax,
                                surchargeTax,
                                totalExcludingTax,
                                totalTax,
                                totalCost,
                                orderStatus,
                                paymentStatus,
                                expectedTotalPreparingDuration,
                                if (createdBy == "0") "9" else createdBy,
                                customerName,
                                orderNote,
                                totalAmountTendered,
                                changeRequired,
                                Date(createdAt),
                                null,
                                Date(updatedAt),
                                waiterId = waiterId,
                                isNewCustomer = isNewCustomer,
                                orderStatusChanges = orderStatusChanges.map { statusChange ->
                                    with(statusChange) {
                                        OrderStatusChangeRequest(
                                            id,
                                            orderId,
                                            status,
                                            changedBy,
                                            Date(createdAt),
                                            null,
                                            Date(updatedAt)
                                        )
                                    }
                                },
                                orderItems = orderItems.map { item ->
                                    with(item) {
                                        OrderItemRequest(
                                            id,
                                            orderId,
                                            productId,
                                            name,
                                            quantity,
                                            isTakeaway,
                                            priceExcludingTax,
                                            tax,
                                            isTaxInclusive,
                                            cost,
                                            preparingDuration,
                                            status,
                                            itemNote,
                                            Date(createdAt),
                                            null,
                                            Date(updatedAt),
                                            itemStatusChanges = itemStatusChangesDao.getItemStatusChangesByItem(id).map { statusChange ->
                                                with(statusChange) {
                                                    ItemStatusChangeRequest(
                                                        id,
                                                        itemId,
                                                        status,
                                                        changedBy,
                                                        Date(createdAt),
                                                        null,
                                                        Date(updatedAt)
                                                    )
                                                }
                                            },
                                            staffs = itemStaffDao.getStaffsFromItem(id).map { staff ->
                                                with(staff) {
                                                    ItemStaffRequest(
                                                        id,
                                                        itemId,
                                                        staffId,
                                                        Date(createdAt),
                                                        null,
                                                        Date(updatedAt)
                                                    )
                                                }
                                            }
                                        )
                                    }
                                },
                                payments = orderPayments.map { payment ->
                                    with(payment) {
                                        OrderPaymentRequest(
                                            id,
                                            orderId,
                                            paymentMethodId,
                                            amount,
                                            referenceNumber,
                                            Date(createdAt),
                                            null,
                                            Date(updatedAt),
                                        )
                                    }
                                },
                                table = orderTable?.run {
                                    OrderTableRequest(
                                        id,
                                        orderId,
                                        tableId,
                                        Date(createdAt),
                                        null,
                                        Date(updatedAt),
                                    )
                                }
                            )
                        }, { request ->
                            onlineRepository.syncOrder(request)
                        }, { data ->
                            orderDao.update(it.apply {
                                synced = true
                            }, false)

                            Timber.i("Order synced $data")
                        }, { code, message ->
                            orderDao.update(it.apply {
                                updatedAt = currentTimestamp+1
                            }, false)

                            Timber.e("Error when syncing order (${it.id}), $code, $message")
                        }
                    )
                }

                orderDataStore.storeLastOrderSync(currentTimestamp)
                delay(5000)
            }
        }
    }

    private fun stopSyncing() {
        Timber.d("Order Sync stopped")
        syncJob?.cancel()
    }

    fun stop() {
        scope?.cancel()
        scope = null
    }
}
