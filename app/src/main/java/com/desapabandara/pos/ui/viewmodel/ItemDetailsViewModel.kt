package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.base.model.ItemInfo
import com.desapabandara.pos.local_db.dao.OrderItemDao
import com.desapabandara.pos.model.ui.ItemDetailsResult
import com.desapabandara.pos.ui.fragment.ARG_CATEGORY_ID
import com.desapabandara.pos.ui.fragment.ARG_ITEM_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    private val fragmentStateEventBus: FragmentStateEventBus,
    private val savedStateHandle: SavedStateHandle,
    private val orderItemDao: OrderItemDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val id = MutableStateFlow("")
    val itemName = MutableStateFlow("")
    val quantity = MutableStateFlow(0)
    val note = MutableStateFlow("")
    val isTakeaway = MutableStateFlow(false)

    init {
        populateItemDetails()
    }

    private fun populateItemDetails() {
        viewModelScope.launch(ioDispatcher) {
            val itemId = savedStateHandle.get<String>(ARG_ITEM_ID)
            if (itemId == null) {
                dismiss()
                return@launch
            }

            id.value = itemId

            val orderItem = orderItemDao.getOrderItemById(itemId)
            if (orderItem == null) {
                dismiss()
                return@launch
            }

            itemName.value = orderItem.name
            quantity.value = orderItem.quantity.toInt()
            note.value = orderItem.itemNote
            isTakeaway.value = orderItem.isTakeaway
        }
    }

    fun increaseQty() {
        viewModelScope.launch(ioDispatcher) {
            quantity.value += 1
        }
    }

    fun decreaseQty() {
        viewModelScope.launch(ioDispatcher) {
            if (quantity.value <= 1) {
                quantity.value = 1
                return@launch
            }
            quantity.value -= 1
        }
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished(ItemDetailsResult.Cancelled)
    }

    fun save() {
        viewModelScope.launch(ioDispatcher) {
            fragmentStateEventBus.currentStateFinished(ItemDetailsResult.Saved(
                ItemInfo(
                    id.value,
                    quantity.value.toDouble(),
                    note.value,
                    isTakeaway.value
                )
            ))
        }
    }
}