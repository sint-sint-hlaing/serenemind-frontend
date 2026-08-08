package com.serenemind.ui.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.JournalRequest
import com.serenemind.model.response.JournalResponse
import com.serenemind.network.NetworkResult
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
            repository.getJournal(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = JournalEditorUiState.Loading
                    is NetworkResult.Success -> {
                        _journal.value = result.data
                        _uiState.value = JournalEditorUiState.Idle
                    }
                    is NetworkResult.Error -> _uiState.value = JournalEditorUiState.Error(result.message)
                }
            }
        }
    }

    fun saveJournal(
        id: Int? = null,
        title: String,
        content: String,
        tags: List<String>,
        favourite: Boolean,
        photoPart: MultipartBody.Part? = null
    ) {
        viewModelScope.launch {
            val request = JournalRequest(title, content, tags, favourite)
            val flow = if (id == null) {
                repository.createJournal(request)
            } else {
                repository.updateJournal(id, request)
            }

            flow.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = JournalEditorUiState.Loading
                    is NetworkResult.Success -> {
                        val savedJournal = result.data
                        val targetId = savedJournal.id
                        
                        if (photoPart != null) {
                            repository.uploadPhoto(targetId, photoPart).collect { photoResult ->
                                when (photoResult) {
                                    is NetworkResult.Success -> _uiState.value = JournalEditorUiState.Success
                                    is NetworkResult.Error -> _uiState.value = JournalEditorUiState.Error("Text saved, but photo failed: ${photoResult.message}")
                                    else -> {}
                                }
                            }
                        } else {
                            _uiState.value = JournalEditorUiState.Success
                        }
                    }
                    is NetworkResult.Error -> _uiState.value = JournalEditorUiState.Error(result.message)
                }
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = JournalEditorUiState.Error(message)
    }

    fun deleteJournal(id: Int) {
        viewModelScope.launch {
            repository.deleteJournal(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = JournalEditorUiState.Loading
                    is NetworkResult.Success -> _uiState.value = JournalEditorUiState.Success
                    is NetworkResult.Error -> _uiState.value = JournalEditorUiState.Error(result.message)
                }
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
