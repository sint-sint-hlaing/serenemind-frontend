package com.serenemind.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.NotificationResponse
import com.serenemind.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class NotificationNavigationEvent {
    data class NavigateToPost(val postId: Long) : NotificationNavigationEvent()
    data class NavigateToComment(val postId: Long) : NotificationNavigationEvent()
    data class NavigateToReminder(val reminderId: Long) : NotificationNavigationEvent()
    object ShowSystemDialog : NotificationNavigationEvent()
}

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NotificationNavigationEvent>()
    val navigationEvent: SharedFlow<NotificationNavigationEvent> = _navigationEvent.asSharedFlow()

    private var currentFilter: String? = null

    init {
        fetchNotifications()
    }

    fun fetchNotifications(filter: String? = null) {
        currentFilter = if (filter == "all" || filter == null) null else filter
        viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            notificationRepository.getNotifications(currentFilter)
                .catch { e ->
                    _uiState.value = NotificationUiState.Error(e.message ?: "Unknown error")
                }
                .collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = NotificationUiState.Success(response.body()!!)
                    } else {
                        _uiState.value = NotificationUiState.Error("Failed to fetch notifications")
                    }
                }
        }
    }

    fun onNotificationClicked(id: Long) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.clickNotification(id)
                if (response.isSuccessful && response.body() != null) {
                    val noti = response.body()!!
                    handleNavigation(noti)
                    refreshNotifications()
                }
            } catch (e: Exception) {
                // Silent error
            }
        }
    }

    private suspend fun handleNavigation(noti: NotificationResponse) {
        val targetId = noti.targetId ?: return
        when (noti.targetType) {
            "POST" -> _navigationEvent.emit(NotificationNavigationEvent.NavigateToPost(targetId))
            "COMMENT" -> _navigationEvent.emit(NotificationNavigationEvent.NavigateToComment(targetId))
            "REMINDER" -> _navigationEvent.emit(NotificationNavigationEvent.NavigateToReminder(targetId))
            "SYSTEM" -> _navigationEvent.emit(NotificationNavigationEvent.ShowSystemDialog)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val response = notificationRepository.markAllAsRead()
                if (response.isSuccessful) {
                    refreshNotifications()
                }
            } catch (e: Exception) {
                // Silently ignore
            }
        }
    }

    private fun refreshNotifications() {
        viewModelScope.launch {
            notificationRepository.getNotifications(currentFilter)
                .catch { /* ignore silent refresh error */ }
                .collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = NotificationUiState.Success(response.body()!!)
                    }
                }
        }
    }
}
