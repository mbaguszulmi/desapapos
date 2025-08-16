package com.desapabandara.pos.model.ui

sealed class SyncStatus {
    data class Syncing(
        val syncNameResource: Int,
    ): SyncStatus()

    data class Error(
        val message: String,
    ): SyncStatus()
}
