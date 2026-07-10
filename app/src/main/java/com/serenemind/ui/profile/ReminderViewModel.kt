package com.serenemind.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.ReminderRequest
import com.serenemind.repository.ReminderRepository
import com.serenemind.util.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReminderUiState>(ReminderUiState.Loading)
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    private val _addUiState = MutableStateFlow<AddReminderUiState>(AddReminderUiState.Idle)
    val addUiState: StateFlow<AddReminderUiState> = _addUiState.asStateFlow()

    init {
        fetchReminders()
    }

    fun fetchReminders(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = ReminderUiState.Loading
            }
            reminderRepository.getReminders().collect { response ->
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ReminderUiState.Success(response.body()!!)
                } else if (showLoading) {
                    _uiState.value = ReminderUiState.Error("Failed to fetch reminders")
                }
            }
        }
    }

    fun toggleReminder(context: Context, id: Long) {
        val currentState = _uiState.value
        if (currentState is ReminderUiState.Success) {
            val updatedReminders = currentState.reminders.map {
                if (it.id == id) {
                    val newEnabled = !it.enabled
                    if (newEnabled) {
                        ReminderScheduler.scheduleReminder(context, it.copy(enabled = true))
                    } else {
                        ReminderScheduler.cancelReminder(context, it.id)
                    }
                    it.copy(enabled = newEnabled)
                } else it
            }
            _uiState.value = ReminderUiState.Success(updatedReminders)

            viewModelScope.launch {
                val response = reminderRepository.toggleReminder(id)
                if (!response.isSuccessful) {
                    _uiState.value = currentState // Rollback
                    // Should also rollback alarm here ideally
                }
            }
        }
    }

    fun createReminder(
        context: Context,
        title: String,
        repeatType: String,
        time: String,
        startDate: String,
        tone: String,
        note: String?,
        enabled: Boolean
    ) {
        viewModelScope.launch {
            _addUiState.value = AddReminderUiState.Loading
            val request = ReminderRequest(title, repeatType, time, startDate, tone, note, enabled)
            val response = reminderRepository.createReminder(request)
            if (response.isSuccessful && response.body() != null) {
                _addUiState.value = AddReminderUiState.Success
                ReminderScheduler.scheduleReminder(context, response.body()!!)
                fetchReminders(showLoading = false)
            } else {
                _addUiState.value = AddReminderUiState.Error("Failed to create reminder")
            }
        }
    }

    fun deleteReminder(context: Context, id: Long) {
        val currentState = _uiState.value
        if (currentState is ReminderUiState.Success) {
            val updatedReminders = currentState.reminders.filter { it.id != id }
            _uiState.value = ReminderUiState.Success(updatedReminders)
            ReminderScheduler.cancelReminder(context, id)

            viewModelScope.launch {
                val response = reminderRepository.deleteReminder(id)
                if (!response.isSuccessful) {
                    _uiState.value = currentState // Rollback
                }
            }
        }
    }

    fun resetAddState() {
        _addUiState.value = AddReminderUiState.Idle
    }
}
