package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.base.util.CurrencyUtil
import com.desapabandara.pos.ui.fragment.ARG_AMOUNT_TENDERED
import com.desapabandara.pos.ui.fragment.ARG_CHANGE_REQUIRED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SaleCompletedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val currencyUtil: CurrencyUtil,
    private val fragmentStateEventBus: FragmentStateEventBus
): ViewModel() {
    val amountTendered = savedStateHandle.getStateFlow(ARG_AMOUNT_TENDERED, 0.0).map {
        currencyUtil.getCurrentFormat(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), currencyUtil.getCurrentFormat(0.0))

    val changeRequired = savedStateHandle.getStateFlow(ARG_CHANGE_REQUIRED, 0.0).map {
        currencyUtil.getCurrentFormat(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), currencyUtil.getCurrentFormat(0.0))

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished()
    }


}
