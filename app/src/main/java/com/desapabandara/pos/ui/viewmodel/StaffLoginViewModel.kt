package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import com.desapabandara.pos.R
import com.desapabandara.pos.base.eventbus.StaffLoginEventBus
import com.desapabandara.pos.local_db.dao.StaffDao
import com.desapabandara.pos.preference.datastore.AuthDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffLoginViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    authDataStore: AuthDataStore,
    private val staffLoginEventBus: StaffLoginEventBus,
    private val staffDao: StaffDao,
    private val uiStatusEventBus: UIStatusEventBus
): NumPadViewModel(
    ioDispatcher,
    NumberType.IntText,
    null,
    { " • ".repeat(it.length) },
    6
) {
    val logoUrl = authDataStore.getCurrentStoreData().map {
        it?.company?.logoUrl ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    override fun confirm() {
        viewModelScope.launch(ioDispatcher) {
            staffDao.getStaffByPin(_text.value)?.let {
                staffLoginEventBus.setCurrentStaff(it)
            } ?: uiStatusEventBus.setUiStatus(UiStatus.ShowError(
                UiMessage.ResourceMessage(R.string.incorrect_pin)
            ))
        }
    }
}