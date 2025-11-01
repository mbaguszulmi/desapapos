package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import com.desapabandara.pos.local_db.dao.LocationDao
import com.desapabandara.pos.local_db.dao.StaffDao
import com.desapabandara.pos.local_db.dao.StaffLocationAssignmentDao
import com.desapabandara.pos.local_db.dao.StaffPositionDao
import com.desapabandara.pos.local_db.entity.StaffLocationAssignmentEntity
import com.desapabandara.pos.model.ui.LocationDisplay
import com.desapabandara.pos.model.ui.StaffAssignmentDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StaffLocationAssignmentViewModel @Inject constructor(
    private val staffLocationAssignmentDao: StaffLocationAssignmentDao,
    private val staffDao: StaffDao,
    private val staffPositionDao: StaffPositionDao,
    private val locationDao: LocationDao,
    private val fragmentStateEventBus: FragmentStateEventBus,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {
    private val _staffAssignments = MutableStateFlow(emptyList<StaffAssignmentDisplay>())
    val staffAssignments = _staffAssignments.asStateFlow()

    val locations = locationDao.getAll().map {
        mutableListOf(
            LocationDisplay(
                id = "",
                intId = 0,
                name = "Unassigned"
            )
        ).apply {
            addAll(it.map {
                LocationDisplay(
                    id = it.id,
                    intId = it.id.toIntOrNull() ?: 0,
                    name = it.name
                )
            })
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadStaffAssignments()
    }

    private fun loadStaffAssignments() {
        viewModelScope.launch(ioDispatcher) {
            combine(
                staffDao.getAll(),
                staffLocationAssignmentDao.getAll()
            ) { staffs, assignments ->
                staffs.mapNotNull {
                    if (!it.isActive) return@mapNotNull null

                    val assignment = assignments.find { a -> a.staffId == it.id }
                    val staffPosition = staffPositionDao.getPosition(it.positionId)
                    val originalLocation = staffPosition?.let { sp -> locationDao.getLocation(sp.locationId) }
                    val assignedLocation = assignment?.let { a -> locationDao.getLocation(a.locationId) }

                    StaffAssignmentDisplay(
                        staffId = it.id,
                        staffName = it.name,
                        positionName = staffPosition?.name ?: "",
                        originalLocationId = originalLocation?.id ?: "",
                        originalLocationName = originalLocation?.name ?: "",
                        assignedLocationId = assignment?.locationId ?: "",
                        assignedLocationName = assignedLocation?.name ?: ""
                    )
                }
            }.flowOn(ioDispatcher).collect {
                _staffAssignments.value = it
            }
        }
    }

    fun assignLocationToStaff(staffId: String, locationId: String) {
        viewModelScope.launch(ioDispatcher) {
            val existingAssignment = staffLocationAssignmentDao.getAssignmentForStaff(staffId)

            if (locationId.isBlank() || locationId == "0") {
                // Remove assignment if locationId is blank
                if (existingAssignment != null) {
                    staffLocationAssignmentDao.delete(existingAssignment)
                }
                return@launch
            }

            if (existingAssignment != null) {
                // Update existing assignment
                staffLocationAssignmentDao.update(existingAssignment.apply {
                    this.locationId = locationId
                })
            } else {
                // Create new assignment
                val newAssignment = StaffLocationAssignmentEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    staffId = staffId,
                    locationId = locationId
                )

                staffLocationAssignmentDao.save(newAssignment)
            }
        }
    }

    fun dismiss() {
        fragmentStateEventBus.currentStateFinished()
    }
}