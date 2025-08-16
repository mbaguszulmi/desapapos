package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentState
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.local_db.dao.ProductCategoryDao
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.base.model.OrderType
import com.desapabandara.pos.model.ui.ProductCategoryDisplay
import com.desapabandara.pos.ui.fragment.CartFragment
import com.desapabandara.pos.base.util.CurrencyUtil
import com.desapabandara.pos.model.ui.CustomerSetResult
import com.desapabandara.pos.model.ui.PrinterDeviceScanResult
import com.desapabandara.pos.model.ui.SetOrderTypeResult
import com.desapabandara.pos.ui.fragment.AddCustomerFragment
import com.desapabandara.pos.ui.fragment.SetOrderTypeFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PosViewModel @Inject constructor(
    productCategoryDao: ProductCategoryDao,
    private val orderManager: OrderManager,
    private val fragmentStateEventBus: FragmentStateEventBus,
    private val currencyUtil: CurrencyUtil,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private var addCustomerJob: Job? = null
    private var setOrderTypeJob: Job? = null

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory = _selectedCategory.asStateFlow()

    val currentOrderNumber = orderManager.currentOrder.map {
        it?.orderNumber?.toString() ?: "N/A"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "N/A")

    val orderItemCount = orderManager.currentOrder.map {
        it?.orderItems?.sumOf { i -> i.quantity.toInt() } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val currentOrderTotal = orderManager.currentOrder.map {
        currencyUtil.getCurrentFormat(it?.total ?: .0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val orderType = orderManager.currentOrder.map {
        it?.orderType ?: OrderType.EatIn
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OrderType.EatIn)

    val tableNumber = orderManager.currentOrder.map {
        it?.orderTable?.table?.name ?: "-"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "-")

    val customerCount = orderManager.currentOrder.map {
        it?.totalCustomerCount ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val customerName = orderManager.currentOrder.map {
        it?.customerName ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val categories = combine(productCategoryDao.getAll(), selectedCategory) { categories, selectedId ->
        categories.map {
            with(it) {
                ProductCategoryDisplay(
                    id, name, selectedId == id
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectCategory(id: String) {
        viewModelScope.launch(ioDispatcher) {
            _selectedCategory.value = id
        }
    }

    fun showOrderItems() {
        fragmentStateEventBus.setCurrentState(CartFragment())
    }

    fun changeOrderType() {
        viewModelScope.launch(ioDispatcher) {
            if (orderManager.currentOrder.value == null) return@launch

            setOrderTypeJob?.cancel()
            setOrderTypeJob = awaitSetOrderTypeResult()
            fragmentStateEventBus.setCurrentState(SetOrderTypeFragment())
        }
    }

    private fun awaitSetOrderTypeResult() = viewModelScope.launch(ioDispatcher) {
        val result = fragmentStateEventBus.awaitStateResult<SetOrderTypeResult>()

        if (result is SetOrderTypeResult.Completed) {
            result.run {
                orderManager.apply {
                    setOrderType(orderType)
                    setTable(tableId)
                }
            }
        }
    }

    fun showAddCustomer() {
        viewModelScope.launch(ioDispatcher) {
            if (orderManager.currentOrder.value == null) return@launch

            addCustomerJob?.cancel()
            addCustomerJob = awaitCustomerResult()
            fragmentStateEventBus.setCurrentState(AddCustomerFragment())
        }
    }

    private fun awaitCustomerResult() = viewModelScope.launch(ioDispatcher) {
        val result = fragmentStateEventBus.awaitStateResult<CustomerSetResult>()

        if (result is CustomerSetResult.Added) {
            orderManager.addCustomer(result.id, result.name, result.phoneNumber, result.adultMaleCount, result.adultFemaleCount, result.childMaleCount, result.childFemaleCount)
        }
    }
}