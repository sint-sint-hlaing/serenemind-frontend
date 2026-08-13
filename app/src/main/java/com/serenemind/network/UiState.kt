package com.serenemind.network

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(
        val message: String,
        val fieldErrors: Map<String, String>? = null
    ) : UiState<Nothing>
}
