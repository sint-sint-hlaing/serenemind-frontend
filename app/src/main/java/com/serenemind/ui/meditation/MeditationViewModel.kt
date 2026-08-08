package com.serenemind.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.MeditationResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.MeditationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
            repository.getDashboard().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = MeditationUiState.Loading
                    is NetworkResult.Success -> _uiState.value = MeditationUiState.Success(result.data)
                    is NetworkResult.Error -> _uiState.value = MeditationUiState.Error(result.message)
                }
            }
        }
    }

    fun fetchRecommendations() {
        viewModelScope.launch {
            repository.getRecommendations().collect { result ->
                if (result is NetworkResult.Success) {
                    _recommendations.value = result.data
                }
            }
        }
    }

    fun fetchContinueListening() {
        viewModelScope.launch {
            repository.getContinueListening().collect { result ->
                if (result is NetworkResult.Success) {
                    _continueListening.value = result.data
                }
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            repository.search(query).collect { result ->
                if (result is NetworkResult.Success) {
                    _searchResults.value = result.data
                }
            }
        }
    }

    fun toggleFavorite(meditationId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(meditationId).collect { result ->
                if (result is NetworkResult.Success) {
                    _selectedMeditation.value = _selectedMeditation.value?.copy(
                        favorite = result.data.favorite
                    )
                }
            }
        }
    }

    fun saveTimer(meditationId: Long, minutes: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            repository.saveTimer(meditationId, minutes).collect { result ->
                when (result) {
                    is NetworkResult.Success -> onResult(result.data.message ?: "Timer set")
                    is NetworkResult.Error -> onResult(result.message)
                    else -> {}
                }
            }
        }
    }

    fun getShareLink(meditationId: Long, onResult: (String) -> Unit) {
        viewModelScope.launch {
            repository.getShareLink(meditationId).collect { result ->
                if (result is NetworkResult.Success) {
                    onResult(result.data.shareUrl ?: "")
                }
            }
        }
    }

    fun navigateToPrevious() {
        val current = _selectedMeditation.value ?: return
        viewModelScope.launch {
            repository.getPrevious(current.id).collect { result ->
                if (result is NetworkResult.Success) {
                    _selectedMeditation.value = result.data
                }
            }
        }
    }

    fun navigateToNext() {
        val current = _selectedMeditation.value ?: return
        viewModelScope.launch {
            repository.getNext(current.id).collect { result ->
                if (result is NetworkResult.Success) {
                    repository.getMeditationById(result.data.id).collect { fullResult ->
                        if (fullResult is NetworkResult.Success) {
                            _selectedMeditation.value = fullResult.data
                        }
                    }
                }
            }
        }
    }

    fun downloadAudio(meditationId: Long, onResult: (String) -> Unit) {
        viewModelScope.launch {
            repository.download(meditationId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> onResult("Download complete!")
                    is NetworkResult.Error -> onResult("Download failed: ${result.message}")
                    else -> {}
                }
            }
        }
    }

    fun selectMeditation(meditation: MeditationResponse) {
        _selectedMeditation.value = meditation
    }
}
