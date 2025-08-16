package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.model.ui.PosProductDisplay
import com.desapabandara.pos.base.repository.ProductRepository
import com.desapabandara.pos.ui.fragment.ARG_CATEGORY_ID
import com.desapabandara.pos.base.util.CurrencyUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PosProductViewModel @Inject constructor(
    private val currencyUtil: CurrencyUtil,
    private val productRepository: ProductRepository,
    private val orderManager: OrderManager,
    savedStateHandle: SavedStateHandle,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    val products = savedStateHandle.getStateFlow(ARG_CATEGORY_ID, "").flatMapLatest {
        productRepository.getProductsByCategoryId(it)
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

    fun addProductToCart(id: String) {
        orderManager.addOrderItem(id)
    }

}