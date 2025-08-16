package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import com.desapabandara.pos.R
import com.desapabandara.pos.base.manager.OrderManager
import com.desapabandara.pos.base.model.OrderType
import com.desapabandara.pos.local_db.dao.TableDao
import com.desapabandara.pos.model.ui.SetOrderTypeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetOrderTypeViewModel @Inject constructor(
    private val orderManager: OrderManager,
    private val fragmentStateEventBus: FragmentStateEventBus,
    private val uiStateEventBus: UIStatusEventBus,
    private val tableDao: TableDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    val tableNumber = MutableStateFlow("")

    val orderType = orderManager.currentOrder.map {
        it?.orderType ?: OrderType.EatIn
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OrderType.EatIn)

    init {
        initializeTableNumber()
    }

    private fun initializeTableNumber() {
        viewModelScope.launch(ioDispatcher) {
            tableNumber.value =  orderManager.currentOrder.value?.orderTable?.table?.name ?: ""
        }
    }

    fun selectType(type: OrderType) {
        viewModelScope.launch(ioDispatcher) {
            when(type) {
                OrderType.EatIn -> {
                    val tableName = tableNumber.value
                    val tableId = if (tableName.isNotBlank()) {
                        val table = tableDao.getTableByName(tableName)
                        if (table == null) {
                            uiStateEventBus.setUiStatus(UiStatus.ShowError(
                                UiMessage.ResourceMessage(R.string.table_is_not_available)
                            ))

                            return@launch
                        }

                        table.id
                    } else ""

                    SetOrderTypeResult.Completed(
                        OrderType.EatIn,
                        tableId
                    )
                }
                OrderType.Takeaway -> {
                    tableNumber.value = ""
                    SetOrderTypeResult.Completed(
                        OrderType.Takeaway,
                        ""
                    )
                }
            }.run {
                fragmentStateEventBus.currentStateFinished(this)
            }
        }
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished(SetOrderTypeResult.None)
    }
}