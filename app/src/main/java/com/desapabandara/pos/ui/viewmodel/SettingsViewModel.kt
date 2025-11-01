package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.model.ui.SettingsScreen
import com.desapabandara.pos.ui.fragment.PrinterSettingsFragment
import com.desapabandara.pos.ui.fragment.StaffAssignmentFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fragmentStateEventBus: FragmentStateEventBus
): ViewModel() {

    fun selectSetting(screen: SettingsScreen) {
        when(screen) {
            SettingsScreen.StaffAssignments -> StaffAssignmentFragment()
            SettingsScreen.Printers -> PrinterSettingsFragment()
            else -> null
        }?.let {
            fragmentStateEventBus.setCurrentState(it)
        }
    }

}