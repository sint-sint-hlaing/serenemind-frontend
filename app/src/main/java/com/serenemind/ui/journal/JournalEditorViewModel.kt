package com.serenemind.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.JournalRequest
import com.serenemind.model.response.JournalResponse
import com.serenemind.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class JournalEditorViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<JournalEditorUiState>(JournalEditorUiState.Idle)
    val uiState: StateFlow<JournalEditorUiState> = _uiState

    private val _journal = MutableStateFlow<JournalResponse?>(null)
    val journal: StateFlow<JournalResponse?> = _journal

    fun loadJournal(id: Int) {
        viewModelScope.launch {
            _uiState.value = JournalEditorUiState.Loading
            try {
                val response = repository.getJournal(id)
                if (response.isSuccessful) {
                    _journal.value = response.body()
                    _uiState.value = JournalEditorUiState.Idle
                } else {
                    _uiState.value = JournalEditorUiState.Error("Failed to load journal")
                }
            } catch (e: Exception) {
                _uiState.value = JournalEditorUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun saveJournal(
        id: Int? = null,
        title: String,
        content: String,
        tags: List<String>?,
        isPrivate: Boolean,
        favourite: Boolean,
        photoPart: MultipartBody.Part? = null
    ) {
        viewModelScope.launch {
            _uiState.value = JournalEditorUiState.Loading
            try {
                val request = JournalRequest(title, content, tags, isPrivate, favourite)
                val response = if (id == null) {
                    repository.createJournal(request)
                } else {
                    repository.updateJournal(id, request)
                }

                if (response.isSuccessful) {
                    val savedJournal = response.body()
                    if (photoPart != null && savedJournal != null) {
                        repository.uploadPhoto(savedJournal.id, photoPart)
                    }
                    _uiState.value = JournalEditorUiState.Success
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}: ${response.message()}"
                    _uiState.value = JournalEditorUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = JournalEditorUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = JournalEditorUiState.Error(message)
    }

    fun deleteJournal(id: Int) {
        viewModelScope.launch {
            _uiState.value = JournalEditorUiState.Loading
            try {
                val response = repository.deleteJournal(id)
                if (response.isSuccessful) {
                    _uiState.value = JournalEditorUiState.Success
                } else {
                    _uiState.value = JournalEditorUiState.Error("Failed to delete journal")
                }
            } catch (e: Exception) {
                _uiState.value = JournalEditorUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class JournalEditorUiState {
    object Idle : JournalEditorUiState()
    object Loading : JournalEditorUiState()
    object Success : JournalEditorUiState()
    data class Error(val message: String) : JournalEditorUiState()
}

class JournalEditorViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalEditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalEditorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
