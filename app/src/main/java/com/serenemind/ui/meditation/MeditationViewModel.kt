package com.serenemind.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.MeditationResponse
import com.serenemind.repository.MeditationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeditationViewModel(private val repository: MeditationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MeditationUiState>(MeditationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _selectedMeditation = MutableStateFlow<MeditationResponse?>(null)
    val selectedMeditation = _selectedMeditation.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MeditationResponse>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _recommendations = MutableStateFlow<List<MeditationResponse>>(emptyList())
    val recommendations = _recommendations.asStateFlow()

    private val _continueListening = MutableStateFlow<List<MeditationResponse>>(emptyList())
    val continueListening = _continueListening.asStateFlow()

    init {
        fetchMeditationDashboard()
        fetchRecommendations()
        fetchContinueListening()
    }

    fun fetchMeditationDashboard() {
        viewModelScope.launch {
            _uiState.value = MeditationUiState.Loading
            repository.getDashboard().collect { response ->
                if (response.isSuccessful) {
                    response.body()?.let {
                        _uiState.value = MeditationUiState.Success(it)
                    } ?: run {
                        _uiState.value = MeditationUiState.Error("Empty response body")
                    }
                } else {
                    _uiState.value = MeditationUiState.Error("Server error: ${response.code()}")
                }
            }
        }
    }

    fun fetchRecommendations() {
        viewModelScope.launch {
            repository.getRecommendations().collect { response ->
                if (response.isSuccessful) {
                    _recommendations.value = response.body() ?: emptyList()
                }
            }
        }
    }

    fun fetchContinueListening() {
        viewModelScope.launch {
            repository.getContinueListening().collect { response ->
                if (response.isSuccessful) {
                    _continueListening.value = response.body() ?: emptyList()
                }
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            repository.search(query).collect { response ->
                if (response.isSuccessful) {
                    _searchResults.value = response.body() ?: emptyList()
                }
            }
        }
    }

    fun toggleFavorite(meditationId: Long) {
        viewModelScope.launch {
            val response = repository.toggleFavorite(meditationId)
            if (response.isSuccessful) {
                // Update selected meditation favorite state
                _selectedMeditation.value = _selectedMeditation.value?.copy(
                    favorite = response.body()?.favorite ?: false
                )
            }
        }
    }

    fun saveTimer(meditationId: Long, minutes: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val response = repository.saveTimer(meditationId, minutes)
            if (response.isSuccessful) {
                onResult(response.body()?.message ?: "Timer set")
            } else {
                onResult("Failed to set timer")
            }
        }
    }

    fun getShareLink(meditationId: Long, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val response = repository.getShareLink(meditationId)
            if (response.isSuccessful) {
                onResult(response.body()?.shareUrl ?: "")
            }
        }
    }

    fun navigateToPrevious() {
        val current = _selectedMeditation.value ?: return
        viewModelScope.launch {
            val response = repository.getPrevious(current.id)
            if (response.isSuccessful) {
                _selectedMeditation.value = response.body()
            }
        }
    }

    fun navigateToNext() {
        val current = _selectedMeditation.value ?: return
        viewModelScope.launch {
            val response = repository.getNext(current.id)
            if (response.isSuccessful) {
                val list = response.body()
                if (list != null) {
                    // Fetch full meditation details if needed, 
                    // or just update title/id if only partial data is returned.
                    // For now, let's assume we need to fetch full details.
                    repository.getMeditationById(list.id).collect { fullResponse ->
                        if (fullResponse.isSuccessful) {
                            _selectedMeditation.value = fullResponse.body()
                        }
                    }
                }
            }
        }
    }

    fun downloadAudio(meditationId: Long, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val response = repository.download(meditationId)
            if (response.isSuccessful) {
                // In a real app, you'd save the Body stream to a file.
                // For now, we simulate success.
                onResult("Download complete!")
            } else {
                onResult("Download failed.")
            }
        }
    }

    fun selectMeditation(meditation: MeditationResponse) {
        _selectedMeditation.value = meditation
    }
}
