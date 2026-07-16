package com.serenemind.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.JournalResponse
import com.serenemind.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JournalListViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _journals = MutableStateFlow<List<JournalResponse>>(emptyList())
    val journals: StateFlow<List<JournalResponse>> = _journals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentFilter = MutableStateFlow("all")
    val currentFilter: StateFlow<String> = _currentFilter

    init {
        loadJournals()
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
        loadJournals(filter)
    }

    fun loadJournals(filter: String = _currentFilter.value, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _isLoading.value = true
            try {
                val response = repository.listJournals(filter)
                if (response.isSuccessful) {
                    _journals.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Failed to load journals: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchJournals(query: String) {
        if (query.isBlank()) {
            loadJournals()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.searchJournals(query)
                if (response.isSuccessful) {
                    _journals.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Search failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleFavorite(id)
                if (response.isSuccessful) {
                    // Refresh list or update item locally
                    loadJournals()
                }
            } catch (e: Exception) {
                _error.value = "Error toggling favorite: ${e.message}"
            }
        }
    }

    fun deleteJournal(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteJournal(id)
                if (response.isSuccessful) {
                    loadJournals()
                } else {
                    _error.value = "Failed to delete journal"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting journal: ${e.message}"
            }
        }
    }
}

class JournalListViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
