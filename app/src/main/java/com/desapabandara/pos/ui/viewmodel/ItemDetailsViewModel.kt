package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import com.desapabandara.pos.R
import com.desapabandara.pos.base.model.ItemInfo
import com.desapabandara.pos.local_db.dao.OrderItemDao
import com.desapabandara.pos.local_db.entity.OrderItemEntity
import com.desapabandara.pos.model.ui.ItemDetailsResult
import com.desapabandara.pos.ui.fragment.ARG_ITEM
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
    private val uiStatusEventBus: UIStatusEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val id = MutableStateFlow("")
    val itemName = MutableStateFlow("")
    val quantity = MutableStateFlow(1)
    val note = MutableStateFlow("")
    val isTakeaway = MutableStateFlow(false)

    init {
        populateItemDetails()
    }

    private fun populateItemDetails() {
        viewModelScope.launch(ioDispatcher) {
            val item = savedStateHandle.get<OrderItemEntity>(ARG_ITEM)
            if (item == null) {
                dismiss()
                return@launch
            }

            id.value = item.id

            itemName.value = item.name
            quantity.emit(item.quantity.toInt())
            note.value = item.itemNote
            isTakeaway.value = item.isTakeaway
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
            if (quantity.value <= 0) {
                uiStatusEventBus.setUiStatus(UiStatus.ShowError(UiMessage.ResourceMessage(
                        R.string.item_quantity_error
                    )))
                return@launch
            }

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