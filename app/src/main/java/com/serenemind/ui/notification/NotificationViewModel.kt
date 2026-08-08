package com.serenemind.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.network.NetworkResult
import com.serenemind.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            notificationRepository.getNotifications(currentFilter).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = NotificationUiState.Loading
                    is NetworkResult.Success -> _uiState.value = NotificationUiState.Success(result.data)
                    is NetworkResult.Error -> _uiState.value = NotificationUiState.Error(result.message)
                }
            }
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id).collect { result ->
                if (result is NetworkResult.Success) {
                    fetchNotifications(currentFilter)
                }
            }
        }
    }

    fun onNotificationClick(id: Long) {
        viewModelScope.launch {
            notificationRepository.clickNotification(id).collect { result ->
                if (result is NetworkResult.Success) {
                    val notification = result.data
                    // Use targetType for navigation as provided in backend response
                    val targetType = notification.targetType ?: notification.type
                    
                    when (targetType?.uppercase()) {
                        "POST", "LIKE", "COMMENT" -> {
                            notification.targetId?.let { postId ->
                                _navigationEvent.emit(NotificationNavigationEvent.NavigateToPost(postId))
                            }
                        }
                        "SYSTEM" -> {
                            _navigationEvent.emit(NotificationNavigationEvent.ShowSystemDialog(notification.message ?: ""))
                        }
                    }
                    // Refresh current list (this will show the notification as read)
                    fetchNotifications(currentFilter)
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead().collect { result ->
                if (result is NetworkResult.Success) {
                    fetchNotifications(currentFilter)
                }
            }
        }
    }
}

sealed interface NotificationNavigationEvent {
    data class NavigateToPost(val postId: Long) : NotificationNavigationEvent
    data class NavigateToComment(val postId: Long, val commentId: Long) : NotificationNavigationEvent
    data class ShowSystemDialog(val message: String) : NotificationNavigationEvent
}
