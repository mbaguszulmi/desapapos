package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.base.repository.ProductRepository
import com.desapabandara.pos.base.util.CurrencyUtil
import com.desapabandara.pos.model.ui.PosProductDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductSearchViewModel @Inject constructor(
    private val currencyUtil: CurrencyUtil,
    private val productRepository: ProductRepository,
    private val orderManager: OrderManager,
    private val fragmentStateEventBus: FragmentStateEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val search = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val products = search.flatMapLatest {
        if (it.isBlank()) emptyFlow()
        else productRepository.getProductsByKeyword(it)
    }.map {
        it.map { product ->
            with(product) {
                PosProductDisplay(
                    id,
                    name,
                    currencyUtil.getCurrentFormat(priceExcludingTax + tax)
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private suspend fun searchProduct(keyword: String) {
        search.emit(keyword)
    }

    fun sendSearchKeys(keyword: String) {
        viewModelScope.launch {
            if (keyword.trim().length >= 3) {
                searchProduct(keyword.trim())
            } else {
                searchProduct("")
            }
        }
    }

    fun selectProduct(id: String) {
        orderManager.addOrderItem(id)
        fragmentStateEventBus.currentStateFinished()
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished()
    }
}