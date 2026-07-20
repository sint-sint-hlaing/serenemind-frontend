package com.serenemind.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.ReminderRequest
import com.serenemind.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.serenemind.util.RefreshSignals

class ReminderViewModel(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReminderUiState>(ReminderUiState.Loading)
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    private val _addUiState = MutableStateFlow<AddReminderUiState>(AddReminderUiState.Idle)
    val addUiState: StateFlow<AddReminderUiState> = _addUiState.asStateFlow()

    init {
        fetchReminders()
        observeRefreshSignals()
    }

    private fun observeRefreshSignals() {
        viewModelScope.launch {
            RefreshSignals.refreshReminders.collect {
                fetchReminders(showLoading = false)
            }
        }
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
            viewModelScope.launch {
                val response = reminderRepository.toggleReminder(id)
                if (response.isSuccessful && response.body() != null) {
                    val updatedReminder = response.body()!!
                    val updatedReminders = currentState.reminders.map {
                        if (it.id == id) updatedReminder else it
                    }
                    _uiState.value = ReminderUiState.Success(updatedReminders)
                    // Note: Local Alarm scheduling removed as we use Firebase only now.
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
        enabled: Boolean,
        repeatDays: String? = null
    ) {
        viewModelScope.launch {
            _addUiState.value = AddReminderUiState.Loading
            val request = ReminderRequest(title, repeatType, time, startDate, tone, note, enabled, repeatDays)
            val response = reminderRepository.createReminder(request)
            if (response.isSuccessful && response.body() != null) {
                _addUiState.value = AddReminderUiState.Success
                fetchReminders(showLoading = false)
                // Note: Local Alarm scheduling removed as we use Firebase only now.
            } else {
                _addUiState.value = AddReminderUiState.Error("Failed to create reminder")
            }
        }
    }

    fun deleteReminder(context: Context, id: Long) {
        val currentState = _uiState.value
        if (currentState is ReminderUiState.Success) {
            viewModelScope.launch {
                val response = reminderRepository.deleteReminder(id)
                if (response.isSuccessful) {
                    val updatedReminders = currentState.reminders.filter { it.id != id }
                    _uiState.value = ReminderUiState.Success(updatedReminders)
                }
            }
        }
    }

    fun resetAddState() {
        _addUiState.value = AddReminderUiState.Idle
    }
}
