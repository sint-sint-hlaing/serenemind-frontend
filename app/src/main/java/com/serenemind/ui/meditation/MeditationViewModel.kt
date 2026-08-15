// MeditationViewModel.kt
package com.serenemind.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.response.MeditationHistoryResponse
import com.serenemind.model.response.MeditationList
import com.serenemind.model.response.MeditationResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.MeditationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeditationViewModel(private val repository: MeditationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MeditationUiState>(MeditationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _selectedMeditation = MutableStateFlow<MeditationResponse?>(null)
    val selectedMeditation = _selectedMeditation.asStateFlow()

    private val _nextMeditation = MutableStateFlow<MeditationList?>(null)
    val nextMeditation = _nextMeditation.asStateFlow()

    private val _previousMeditation = MutableStateFlow<MeditationResponse?>(null)
    val previousMeditation = _previousMeditation.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MeditationResponse>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _recommendations = MutableStateFlow<List<MeditationResponse>>(emptyList())
    val recommendations = _recommendations.asStateFlow()

    private val _continueListening = MutableStateFlow<List<MeditationResponse>>(emptyList())
    val continueListening = _continueListening.asStateFlow()

    private val _history = MutableStateFlow<List<MeditationHistoryResponse>>(emptyList())
    val history = _history.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        fetchMeditationDashboard()
        fetchRecommendations()
        fetchContinueListening()
        fetchHistory()
    }

    // ===== DASHBOARD =====
    fun fetchMeditationDashboard() {
        viewModelScope.launch {
            repository.getDashboard().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = MeditationUiState.Loading
                    is NetworkResult.Success -> _uiState.value = MeditationUiState.Success(result.data)
                    is NetworkResult.Error -> {
                        _uiState.value = MeditationUiState.Error(result.message)
                        _errorMessage.value = result.message
                    }
                }
            }
        }
    }

    // ===== RECOMMENDATIONS =====
    fun fetchRecommendations() {
        viewModelScope.launch {
            repository.getRecommendations().collect { result ->
                if (result is NetworkResult.Success) {
                    _recommendations.value = result.data
                } else if (result is NetworkResult.Error) {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    // ===== CONTINUE LISTENING =====
    fun fetchContinueListening() {
        viewModelScope.launch {
            repository.getContinueListening().collect { result ->
                if (result is NetworkResult.Success) {
                    _continueListening.value = result.data
                } else if (result is NetworkResult.Error) {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    // ===== SEARCH =====
    fun search(query: String?) {
        viewModelScope.launch {
            repository.searchMeditations(query, null, null).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _searchResults.value = result.data
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== SEARCH WITH FILTERS =====
    fun searchMeditations(query: String?, category: String?, time: String?) {
        viewModelScope.launch {
            repository.searchMeditations(query, category, time).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _searchResults.value = result.data
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== GET MEDITATION BY ID =====
    fun getMeditationById(id: Long) {
        viewModelScope.launch {
            repository.getMeditationById(id).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _selectedMeditation.value = result.data
                        getNextMeditation(id)
                        getPreviousMeditation(id)
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== GET NEXT MEDITATION =====
    fun getNextMeditation(id: Long?) {
        if (id == null) return
        viewModelScope.launch {
            repository.getNext(id).collect { result ->
                if (result is NetworkResult.Success) {
                    _nextMeditation.value = result.data
                } else if (result is NetworkResult.Error) {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    // ===== GET PREVIOUS MEDITATION =====
    fun getPreviousMeditation(id: Long?) {
        if (id == null) return
        viewModelScope.launch {
            repository.getPrevious(id).collect { result ->
                if (result is NetworkResult.Success) {
                    _previousMeditation.value = result.data
                } else if (result is NetworkResult.Error) {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    // ===== TOGGLE FAVORITE =====
    fun toggleFavorite(meditationId: Long?) {
        if (meditationId == null) return
        viewModelScope.launch {
            repository.toggleFavorite(meditationId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val current = _selectedMeditation.value
                        if (current?.id == meditationId) {
                            _selectedMeditation.value = current.copy(favorite = result.data.favorite)
                        }
                        // Update in lists
                        updateFavoriteInLists(meditationId, result.data.favorite)
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateFavoriteInLists(meditationId: Long?, isFavorite: Boolean) {
        // Update search results
        _searchResults.value = _searchResults.value.map {
            if (it.id == meditationId) it.copy(favorite = isFavorite) else it
        }
        // Update recommendations
        _recommendations.value = _recommendations.value.map {
            if (it.id == meditationId) it.copy(favorite = isFavorite) else it
        }
        // Update continue listening
        _continueListening.value = _continueListening.value.map {
            if (it.id == meditationId) it.copy(favorite = isFavorite) else it
        }
    }

    // ===== SAVE TIMER =====
    fun saveTimer(meditationId: Long?, minutes: Int, onResult: (String) -> Unit) {
        if (meditationId == null) {
            onResult("Invalid meditation ID")
            return
        }
        viewModelScope.launch {
            repository.saveTimer(meditationId, minutes).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        onResult(result.data.message ?: "Timer set")
                    }
                    is NetworkResult.Error -> {
                        onResult(result.message ?: "Failed to set timer")
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== GET SHARE LINK =====
    fun getShareLink(meditationId: Long?, onResult: (String) -> Unit) {
        if (meditationId == null) {
            onResult("")
            return
        }
        viewModelScope.launch {
            repository.getShareLink(meditationId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        onResult(result.data.shareUrl ?: "")
                    }
                    is NetworkResult.Error -> {
                        onResult("")
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== NAVIGATE TO PREVIOUS =====
    fun navigateToPrevious() {
        val current = _selectedMeditation.value ?: return
        val currentId = current.id ?: return
        viewModelScope.launch {
            repository.getPrevious(currentId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _selectedMeditation.value = result.data
                        getPreviousMeditation(result.data.id)
                        getNextMeditation(result.data.id)
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== NAVIGATE TO NEXT =====
    fun navigateToNext() {
        val current = _selectedMeditation.value ?: return
        val currentId = current.id ?: return
        viewModelScope.launch {
            repository.getNext(currentId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val nextId = result.data.id ?: return@collect
                        repository.getMeditationById(nextId).collect { fullResult ->
                            when (fullResult) {
                                is NetworkResult.Success -> {
                                    _selectedMeditation.value = fullResult.data
                                    getPreviousMeditation(nextId)
                                    getNextMeditation(nextId)
                                }
                                is NetworkResult.Error -> {
                                    _errorMessage.value = fullResult.message
                                }
                                else -> {}
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== DOWNLOAD AUDIO =====
    fun downloadAudio(meditationId: Long?, onResult: (String) -> Unit) {
        if (meditationId == null) {
            onResult("Invalid meditation ID")
            return
        }
        viewModelScope.launch {
            repository.downloadAudio(meditationId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        onResult("Download complete!")
                    }
                    is NetworkResult.Error -> {
                        onResult("Download failed: ${result.message}")
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== COMPLETE SESSION =====
    fun completeSession(meditationId: Long, durationMinutes: Int?, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val request = MeditationSessionRequest(
                meditationId = meditationId,
                durationMinutes = durationMinutes,
                completed = true,
                progressPercentage = 100
            )
            repository.completeSession(request).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        onResult("Session completed! 🎉")
                        fetchMeditationDashboard()
                    }
                    is NetworkResult.Error -> {
                        onResult("Failed: ${result.message}")
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== GET HISTORY =====
    fun fetchHistory() {
        viewModelScope.launch {
            repository.getHistory().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _history.value = result.data
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    fun getHistory(onResult: (List<MeditationHistoryResponse>) -> Unit) {
        viewModelScope.launch {
            repository.getHistory().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        onResult(result.data)
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                        onResult(emptyList())
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== GET BY CATEGORY =====
    fun getByCategory(category: String) {
        viewModelScope.launch {
            repository.getByCategory(category).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _searchResults.value = result.data
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== GET BY TIME =====
    fun getByTime(time: String) {
        viewModelScope.launch {
            repository.getByTime(time).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _searchResults.value = result.data
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    // ===== SELECT MEDITATION =====
    fun selectMeditation(meditation: MeditationResponse) {
        _selectedMeditation.value = meditation
        meditation.id?.let { getMeditationById(it) }
    }

    // ===== CLEAR SEARCH =====
    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    // ===== CLEAR ERROR =====
    fun clearError() {
        _errorMessage.value = null
    }
}

