package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import co.mbznetwork.android.base.util.DateUtil
import com.desapabandara.pos.base.eventbus.OrderPrintEventBus
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.base.model.ItemStatus
import com.desapabandara.pos.base.model.Order
import com.desapabandara.pos.base.model.OrderPrintJob
import com.desapabandara.pos.base.model.OrderStatus
import com.desapabandara.pos.base.model.OrderType
import com.desapabandara.pos.base.util.CurrencyUtil
import com.desapabandara.pos.local_db.dao.OrderDao
import com.desapabandara.pos.model.ui.OrderItemDisplay
import com.desapabandara.pos.ui.fragment.ARG_ORDER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderManager: OrderManager,
    private val currencyUtil: CurrencyUtil,
    private val savedStateHandle: SavedStateHandle,
    private val orderPrintEventBus: OrderPrintEventBus,
    private val fragmentStateEventBus: FragmentStateEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val order = MutableStateFlow<Order?>(null)

    val invoiceNumber = order.map {
        it?.invoiceNumber ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderNumber = order.map {
        it?.orderNumber?.toString() ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val itemCount = order.map {
        it?.orderItems?.sumOf { i -> i.quantity }?.toInt() ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val orderType = order.map {
        it?.orderType ?: OrderType.EatIn
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OrderType.EatIn)

    val tableNumber = order.map {
        it?.orderTable?.table?.name?.let { tableName ->
            ("Table $tableName")
        } ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val staffName = order.map {
        it?.staff?.name ?: "-"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val waiterName = order.map {
        it?.waiter?.name ?: "-"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val customerName = order.map {
        it?.customerName ?: "-"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderTimestamp = order.map {
        it?.createdAt?.let { d -> DateUtil.formatDateTimeShort(d) } ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderStatus = order.map {
        it?.orderStatus ?: OrderStatus.Active
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OrderStatus.Active)

    val orderSubtotal = order.map {
        it?.subtotal?.let { s -> currencyUtil.getCurrentFormat(s) } ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderDiscount = order.map {
        it?.discount?.let { s -> currencyUtil.getCurrentFormat(s) } ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderTax = order.map {
        it?.totalTax?.let { s -> currencyUtil.getCurrentFormat(s) } ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderTotal = order.map {
        it?.total?.let { s -> currencyUtil.getCurrentFormat(s) } ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val items = order.map {
        it?.orderItems?.flatMap { item ->
            with(item) {
                sequence {
                    yield(
                        OrderItemDisplay.ItemDetailed(
                        id,
                        name,
                        currencyUtil.getCurrentFormat((priceExcludingTax + tax) * quantity),
                        quantity.toInt().toString(),
                        isTakeaway,
                        canChangeStatus = staffs.isNotEmpty() && status.id <= ItemStatus.Served.id,
                        isPrepared = status.id >= ItemStatus.Prepared.id,
                        isServed = status.id >= ItemStatus.Served.id
                    ))

                    if (itemNote.isNotBlank()) {
                        yield(
                            OrderItemDisplay.Note(
                            id, itemNote
                        ))
                    }
                }
            }
        } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        monitorOrder()
    }

    private fun monitorOrder() {
        viewModelScope.launch(ioDispatcher) {
            savedStateHandle.getStateFlow<String?>(ARG_ORDER_ID, null).flatMapLatest {
                it?.let {
                    orderManager.getOrderDataDisplayFlow(it)
                } ?: emptyFlow()
            }.collect {
                order.value = it
            }
        }
    }

    fun reprintOrder() {
        viewModelScope.launch(ioDispatcher) {
            order.value?.let {
                orderPrintEventBus.publishJob(OrderPrintJob(
                    it,
                    true,
                    receiptOnly = it.orderStatus.id >= OrderStatus.Completed.id
                ))
            }
        }
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished()
    }

    fun toggleItemPrepared(item: OrderItemDisplay.ItemDetailed) {
        viewModelScope.launch(ioDispatcher) {
            if (!item.canChangeStatus || item.isServed) return@launch

            order.value?.id?.let {
                orderManager.updateOrderItemStatus(it, item.id, if (item.isPrepared) ItemStatus.Sent else ItemStatus.Prepared)
            }
        }
    }

    fun toggleItemServed(item: OrderItemDisplay.ItemDetailed) {
        viewModelScope.launch(ioDispatcher) {
            if (!item.canChangeStatus || !item.isPrepared) return@launch

            order.value?.id?.let {
                orderManager.updateOrderItemStatus(it, item.id, if (item.isServed) ItemStatus.Prepared else ItemStatus.Served)
            }
        }
    }
}