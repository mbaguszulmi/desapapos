package com.desapabandara.pos.base.eventbus

import com.desapabandara.pos.local_db.entity.StaffEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffLoginEventBus @Inject constructor() {
    private val _currentStaff = MutableStateFlow<StaffEntity?>(null)
    val currentStaff = _currentStaff.asStateFlow()

    fun setCurrentStaff(staff: StaffEntity?) {
        _currentStaff.value = staff
    }
}