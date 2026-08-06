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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isBackgroundLoading = MutableStateFlow(false)
    val isBackgroundLoading: StateFlow<Boolean> = _isBackgroundLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentFilter = MutableStateFlow("all")
    val currentFilter: StateFlow<String> = _currentFilter

    init {
        loadJournals()
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
        // Use BACKGROUND mode for tab switching to show the top line animation instead of full shimmer
        loadJournals(filter, mode = LoadType.BACKGROUND)
    }

    fun loadJournals(
        filter: String = _currentFilter.value,
        mode: LoadMode = LoadType.INITIAL
    ) {
        viewModelScope.launch {
            when (mode) {
                LoadType.INITIAL -> _isLoading.value = true
                LoadType.REFRESH -> _isRefreshing.value = true
                LoadType.BACKGROUND -> _isBackgroundLoading.value = true
            }
            
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
                _isRefreshing.value = false
                _isBackgroundLoading.value = false
            }
        }
    }

    enum class LoadType : LoadMode {
        INITIAL, REFRESH, BACKGROUND
    }

    interface LoadMode


    fun searchJournals(query: String) {
        if (query.isBlank()) {
            loadJournals()
            return
        }
        viewModelScope.launch {
            // Use background loading for search to keep it smooth
            _isBackgroundLoading.value = true
            try {
                // Ensure query is trimmed and handle hashtag searches
                val cleanedQuery = query.trim()
                val response = repository.searchJournals(cleanedQuery)
                if (response.isSuccessful) {
                    _journals.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Search failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isBackgroundLoading.value = false
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
            _isBackgroundLoading.value = true
            try {
                val response = repository.deleteJournal(id)
                if (response.isSuccessful) {
                    // Force a BACKGROUND load to keep the animation visible while fetching fresh list
                    loadJournals(mode = LoadType.BACKGROUND)
                } else {
                    _error.value = "Failed to delete journal"
                    _isBackgroundLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error deleting journal: ${e.message}"
                _isBackgroundLoading.value = false
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
