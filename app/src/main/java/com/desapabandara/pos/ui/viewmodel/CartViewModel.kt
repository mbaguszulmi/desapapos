package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.model.ui.OrderItemDisplay
import com.desapabandara.pos.base.util.CurrencyUtil
import com.desapabandara.pos.local_db.dao.OrderItemDao
import com.desapabandara.pos.model.ui.ItemDetailsResult
import com.desapabandara.pos.ui.fragment.ItemDetailsFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val orderManager: OrderManager,
    private val currencyUtil: CurrencyUtil,
    private val fragmentStateEventBus: FragmentStateEventBus,
    private val orderItemDao: OrderItemDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private var itemDetailsJob: Job? = null

    val orderItems = orderManager.currentOrder.map {
        it?.orderItems?.flatMap { item ->
            with(item) {
                sequence {
                    yield(OrderItemDisplay.Item(
                        id,
                        name,
                        currencyUtil.getCurrentFormat((priceExcludingTax + tax) * quantity),
                        quantity.toInt().toString(),
                        isTakeaway
                    ))

                    if (itemNote.isNotBlank()) {
                        yield(OrderItemDisplay.Note(
                            id, itemNote
                        ))
                    }
                }
            }
        } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val orderSubtotal = orderManager.currentOrder.map {
        currencyUtil.getCurrentFormat(it?.subtotal ?: .0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderTax = orderManager.currentOrder.map {
        currencyUtil.getCurrentFormat(it?.subtotalTax ?: .0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderTotal = orderManager.currentOrder.map {
        currencyUtil.getCurrentFormat(it?.total ?: .0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    init {
        observeCurrentOrder()
    }

    private fun observeCurrentOrder() {
        viewModelScope.launch(ioDispatcher) {
            orderManager.currentOrder.collect {
                if (it == null) {
                    fragmentStateEventBus.currentStateFinished()
                }
            }
        }
    }

    fun clearOrder() {
        orderManager.clearCurrentOrder()
    }

    fun sendOrder() {
        orderManager.sendOrder()
    }

    fun payOrder() {
        orderManager.payOrder()
    }

    fun removeItem(id: String) {
        orderManager.removeOrderItem(id)
    }

    fun showItemDetails(id: String) {
        viewModelScope.launch(ioDispatcher) {
            itemDetailsJob?.cancel()
            itemDetailsJob = awaitItemDetailsResult()
            val item = orderItemDao.getOrderItemById(id) ?: return@launch
            fragmentStateEventBus.setCurrentState(ItemDetailsFragment.newInstance(item), true)
        }
    }

    private fun awaitItemDetailsResult() = viewModelScope.launch(ioDispatcher) {
        val result = fragmentStateEventBus.awaitStateResult<ItemDetailsResult>()

        if (result is ItemDetailsResult.Saved) {
            orderManager.addItemInfo(result.info)
        }
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished()
    }

}