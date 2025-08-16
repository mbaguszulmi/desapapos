package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.model.ui.CustomerSetResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val orderManager: OrderManager,
    private val fragmentStateEventBus: FragmentStateEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    val customerName = MutableStateFlow("")
    val customerPhone = MutableStateFlow("")
    val adultMaleCount = MutableStateFlow(0)
    val adultFemaleCount = MutableStateFlow(0)
    val childMaleCount = MutableStateFlow(0)
    val childFemaleCount = MutableStateFlow(0)

    init {
        populateFields()
    }

    private fun populateFields() {
        viewModelScope.launch(ioDispatcher) {
            orderManager.currentOrder.value?.let { order ->
                customerName.value = order.customerName
                customerPhone.value = order.customer?.customer?.phone ?: ""
                adultMaleCount.value = order.adultMaleCount
                adultFemaleCount.value = order.adultFemaleCount
                childMaleCount.value = order.childMaleCount
                childFemaleCount.value = order.childFemaleCount
            }
        }
    }

    fun addCustomer() {
        viewModelScope.launch(ioDispatcher) {
            fragmentStateEventBus.currentStateFinished(CustomerSetResult.Added(
                "",
                customerName.value,
                customerName.value,
                adultMaleCount.value,
                adultFemaleCount.value,
                childMaleCount.value,
                childFemaleCount.value
            ))
        }
    }

    fun dismiss() {
        viewModelScope.launch(ioDispatcher) {
            fragmentStateEventBus.currentStateFinished(CustomerSetResult.Cancelled)
        }
    }
}