package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import com.desapabandara.pos.R
import com.desapabandara.pos.base.eventbus.OrderPaymentEventBus
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.base.model.OrderPaymentEvent
import com.desapabandara.pos.base.model.PaymentMethodType
import com.desapabandara.pos.base.util.CurrencyUtil
import com.desapabandara.pos.local_db.dao.OrderDao
import com.desapabandara.pos.local_db.dao.PaymentMethodDao
import com.desapabandara.pos.model.ui.PaymentMethodDisplay
import com.desapabandara.pos.ui.fragment.SaleCompletedFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val currencyUtil: CurrencyUtil,
    private val paymentEventBus: OrderPaymentEventBus,
    private val paymentMethodDao: PaymentMethodDao,
    private val fragmentStateEventBus: FragmentStateEventBus,
    private val uiStatusEventBus: UIStatusEventBus,
    private val orderManager: OrderManager,
    private val orderDao: OrderDao
): NumPadViewModel(ioDispatcher, NumberType.Decimal, {
    currencyUtil.getCurrentFormat(it.toDouble())
}, null) {

    private val lastOrderId = MutableStateFlow("")

    init {
        monitorCurrentOrder()
        monitorPaymentTransactions()
    }

    val paymentMethods = paymentMethodDao.getAll().map {
        it.map { method ->
            PaymentMethodDisplay(
                id = method.id,
                name = method.name,
                paymentMethodType = method.paymentMethodType,
                logo = when (method.paymentMethodType) {
                    PaymentMethodType.Cash.id -> R.drawable.ic_cash
                    PaymentMethodType.Qris.id -> R.drawable.ic_qris
                    PaymentMethodType.Debit.id -> R.drawable.ic_card
                    PaymentMethodType.Credit.id -> R.drawable.ic_card
                    else -> R.drawable.ic_other_payment
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private fun monitorCurrentOrder() {
        viewModelScope.launch(ioDispatcher) {
            orderManager.currentOrder.collect {
                it?.let {
                    lastOrderId.value = it.id
                    setInitialNumber(it.total)
                } ?: setInitialNumber(0)
            }
        }
    }

    private fun monitorPaymentTransactions() {
        viewModelScope.launch(ioDispatcher) {
            combine(
                paymentEventBus.orderPaymentEvent.filterIsInstance<OrderPaymentEvent.Settled>(),
                lastOrderId,
                orderManager.currentOrder
            ) { settledEvent, lastOrderId, currentOrder ->
                if (currentOrder == null && settledEvent.orderId == lastOrderId) {
                    settledEvent
                } else {
                    null
                }
            }.filterNotNull().collect {
                val currentOrder = orderDao.getOrderById(lastOrderId.value) ?: run {
                    fragmentStateEventBus.currentStateFinished()
                    return@collect
                }

                fragmentStateEventBus.setCurrentState(SaleCompletedFragment.newInstance(
                    currentOrder.totalAmountTendered,
                    currentOrder.changeRequired
                ))
            }
        }
    }

    fun selectPayment(paymentMethod: PaymentMethodDisplay) {
        viewModelScope.launch(ioDispatcher) {
            val amountTendered = numberCombined.value.toDouble()
            val currentOrder = orderManager.currentOrder.value ?: return@launch
            val method = paymentMethodDao.getPaymentMethod(paymentMethod.id) ?: return@launch

            if (amountTendered < currentOrder.total) {
                uiStatusEventBus.showErrorMessage(R.string.amount_tendered_is_less_than_the_order_total)
                return@launch
            }

            paymentEventBus.publish(
                OrderPaymentEvent.Started(
                    orderId = currentOrder.id,
                    amount = amountTendered,
                    paymentMethod = method
                )
            )

        }
    }

    override fun confirm() {}

    fun cancel() {
        fragmentStateEventBus.currentStateFinished()
    }
}