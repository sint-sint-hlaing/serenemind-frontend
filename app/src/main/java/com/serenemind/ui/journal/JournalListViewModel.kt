package com.serenemind.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.JournalResponse
import com.serenemind.network.NetworkResult
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
            repository.listJournals(filter).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        when (mode) {
                            LoadType.INITIAL -> _isLoading.value = true
                            LoadType.REFRESH -> _isRefreshing.value = true
                            LoadType.BACKGROUND -> _isBackgroundLoading.value = true
                        }
                    }
                    is NetworkResult.Success -> {
                        _journals.value = result.data
                        _error.value = null
                        _isLoading.value = false
                        _isRefreshing.value = false
                        _isBackgroundLoading.value = false
                    }
                    is NetworkResult.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                        _isRefreshing.value = false
                        _isBackgroundLoading.value = false
                    }
                }
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
            repository.searchJournals(query.trim()).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _isBackgroundLoading.value = true
                    is NetworkResult.Success -> {
                        _journals.value = result.data
                        _error.value = null
                        _isBackgroundLoading.value = false
                    }
                    is NetworkResult.Error -> {
                        _error.value = result.message
                        _isBackgroundLoading.value = false
                    }
                }
            }
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(id).collect { result ->
                if (result is NetworkResult.Success) {
                    loadJournals()
                } else if (result is NetworkResult.Error) {
                    _error.value = result.message
                }
            }
        }
    }

    fun deleteJournal(id: Int) {
        viewModelScope.launch {
            repository.deleteJournal(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _isBackgroundLoading.value = true
                    is NetworkResult.Success -> loadJournals(mode = LoadType.BACKGROUND)
                    is NetworkResult.Error -> {
                        _error.value = result.message
                        _isBackgroundLoading.value = false
                    }
                }
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
