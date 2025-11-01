package com.desapabandara.pos.model.ui

data class StaffAssignmentDisplay(
    val staffId: String,
    val staffName: String,
    val positionName: String,
    val originalLocationId: String,
    val originalLocationName: String,
    val assignedLocationId: String,
    val assignedLocationName: String
)