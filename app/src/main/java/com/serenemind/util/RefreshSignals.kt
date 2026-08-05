package com.serenemind.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object RefreshSignals {
    private val _refreshReminders = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val refreshReminders = _refreshReminders.asSharedFlow()

    private val _refreshDashboard = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val refreshDashboard = _refreshDashboard.asSharedFlow()

    fun signalRefreshReminders() {
        _refreshReminders.tryEmit(Unit)
    }

    fun signalRefreshDashboard() {
        _refreshDashboard.tryEmit(Unit)
    }
}
