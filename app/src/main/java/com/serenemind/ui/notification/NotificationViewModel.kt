package com.serenemind.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.NotificationResponse
import com.serenemind.repository.NotificationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class NotificationNavigationEvent {
    data class NavigateToPost(val postId: Long) : NotificationNavigationEvent()
    data class NavigateToComment(val postId: Long) : NotificationNavigationEvent()
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
        currentFilter = filter?.takeIf { it != "all" }
        
        viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            notificationRepository.getNotifications(currentFilter)
                .catch { e ->
                    _uiState.value = NotificationUiState.Error(e.message ?: "Unknown error")
                }
                .collect { response ->
                    if (response.isSuccessful) {
                        _uiState.value = response.body()?.let { NotificationUiState.Success(it) } 
                            ?: NotificationUiState.Error("Empty response")
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
                if (response.isSuccessful) {
                    response.body()?.let { handleNavigation(it) }
                    refreshNotifications()
                }
            } catch (e: Exception) {
                // Silent error
            }
        }
    }

    private suspend fun handleNavigation(noti: NotificationResponse) {
        val targetId = noti.targetId ?: return
        val event = when (noti.targetType) {
            "POST" -> NotificationNavigationEvent.NavigateToPost(targetId)
            "COMMENT" -> NotificationNavigationEvent.NavigateToComment(targetId)
            "SYSTEM" -> NotificationNavigationEvent.ShowSystemDialog
            else -> null
        }
        event?.let { _navigationEvent.emit(it) }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                if (notificationRepository.markAllAsRead().isSuccessful) {
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
                    if (response.isSuccessful) {
                        response.body()?.let { _uiState.value = NotificationUiState.Success(it) }
                    }
                }
        }
    }
}
