package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import co.mbznetwork.android.base.util.DateUtil
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.base.model.OrderType
import com.desapabandara.pos.base.util.CurrencyUtil
import com.desapabandara.pos.local_db.dao.OrderDao
import com.desapabandara.pos.local_db.dao.OrderTableDao
import com.desapabandara.pos.local_db.dao.TableDao
import com.desapabandara.pos.model.ui.HeldOrderDisplay
import com.desapabandara.pos.ui.fragment.OrderDetailFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HeldOrderViewModel @Inject constructor(
    private val orderManager: OrderManager,
    private val orderDao: OrderDao,
    private val orderTableDao: OrderTableDao,
    private val tableDao: TableDao,
    private val currencyUtil: CurrencyUtil,
    private val fragmentStateEventBus: FragmentStateEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _showPos = MutableSharedFlow<Boolean>()
    val showPos = _showPos.asSharedFlow()

    private val _heldOrders = MutableStateFlow(emptyList<HeldOrderDisplay>())
    val heldOrders = _heldOrders.asStateFlow()

    init {
        monitorHeldOrders()
    }

    private fun monitorHeldOrders() {
        viewModelScope.launch(ioDispatcher) {
            orderDao.getHeldOrders().map {
                it.map { order ->
                    with(order) {
                        val table = orderTableDao.getOrderTableByOrder(id)?.let { ot ->
                            tableDao.getTable(ot.tableId)
                        }
                        HeldOrderDisplay(
                            id,
                            orderNumber.toString(),
                            DateUtil.formatDateTimeShort(Date(createdAt)),
                            OrderType.fromId(orderType) ?: OrderType.EatIn,
                            table?.name ?: "",
                            currencyUtil.getCurrentFormat(totalExcludingTax + totalTax),
                            synced
                        )
                    }
                }
            }.flowOn(ioDispatcher).collect {
                _heldOrders.emit(it)
            }
        }
    }

    fun loadOrder(id: String) {
        orderManager.activateOrder(id)
        viewModelScope.launch(ioDispatcher) {
            _showPos.emit(true)
        }
    }

    fun showOrderDetail(id: String) {
        fragmentStateEventBus.setCurrentState(OrderDetailFragment.newInstance(id))
    }


}