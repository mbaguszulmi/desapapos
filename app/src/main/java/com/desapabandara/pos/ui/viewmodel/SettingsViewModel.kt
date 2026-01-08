package com.desapabandara.pos.ui.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import co.mbznetwork.android.base.model.DeviceType
import com.desapabandara.pos.model.ui.SettingsScreen
import com.desapabandara.pos.ui.fragment.PrinterSettingsFragment
import com.desapabandara.pos.ui.fragment.StaffAssignmentFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fragmentStateEventBus: FragmentStateEventBus,
    private val deviceType: DeviceType,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _activeSettingScreen = MutableStateFlow<SettingsScreen?>(null)
    val activeSettingScreen = _activeSettingScreen.asStateFlow()

    private val _settingsFragment = MutableStateFlow<Fragment?>(null)
    val settingsFragment = _settingsFragment.asStateFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            if (deviceType == DeviceType.Tablet) {
                selectSetting(SettingsScreen.General)
            }
        }
    }

    fun selectSetting(screen: SettingsScreen) {
        viewModelScope.launch(ioDispatcher) {
            _activeSettingScreen.value = screen

            when(screen) {
                SettingsScreen.StaffAssignments -> StaffAssignmentFragment()
                SettingsScreen.Printers -> PrinterSettingsFragment()
                else -> null
            }?.let {
                if (deviceType == DeviceType.Tablet) {
                    _settingsFragment.value = it
                    return@launch
                }

                fragmentStateEventBus.setCurrentState(it)
            }
        }
    }

}