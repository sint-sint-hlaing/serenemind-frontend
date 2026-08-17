// MeditationViewModel.kt
package com.serenemind.ui.meditation

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.response.MeditationHistoryResponse
import com.serenemind.model.response.MeditationList
import com.serenemind.model.response.MeditationResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.MeditationRepository
import com.serenemind.util.MeditationAlarmScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeditationViewModel(
    private val repository: MeditationRepository,
    private val scheduler: MeditationAlarmScheduler
) : ViewModel() {

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

    private val _downloadState = MutableStateFlow<DownloadUiState>(DownloadUiState.Idle)
    val downloadState = _downloadState.asStateFlow()

    // ===== TIMER STATE =====
    private val _timerUiState = MutableStateFlow(TimerUiState())
    val timerUiState = _timerUiState.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _isTimerCompleted = MutableStateFlow(false)
    val isTimerCompleted = _isTimerCompleted.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private var timerJob: Job? = null
    private var initialTimerSeconds: Int = 0
    private var endTimeMillis: Long = 0L

    init {
        fetchMeditationDashboard()
        fetchRecommendations()
        fetchContinueListening()
        fetchHistory()
        searchMeditations(null, null, null)
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

    fun saveTimer(meditationId: Long?, minutes: Int, onResult: (String) -> Unit) {
        if (meditationId == null) {
            onResult("Invalid meditation ID")
            return
        }
        viewModelScope.launch {
            _timerUiState.update { it.copy(isSaving = true, error = null, selectedMinutes = minutes) }
            repository.saveTimer(meditationId, minutes).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val durationMillis = minutes * 60_000L
                        endTimeMillis = System.currentTimeMillis() + durationMillis
                        
                        scheduler.schedule(meditationId, endTimeMillis)
                        
                        _timerUiState.update { it.copy(
                            isSaving = false,
                            isRunning = true,
                            remainingMillis = durationMillis,
                            successMessage = result.data.message
                        ) }
                        
                        _timerSeconds.value = minutes * 60
                        _isTimerCompleted.value = false
                        _isPlaying.value = true
                        
                        startLocalCountdown()
                        onResult(result.data.message ?: "Timer set")
                    }
                    is NetworkResult.Error -> {
                        _timerUiState.update { it.copy(isSaving = false, error = result.message) }
                        onResult(result.message ?: "Failed to set timer")
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
        }
    }

    private fun startLocalCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val remaining = endTimeMillis - now
                
                if (remaining <= 0) {
                    _timerUiState.update { it.copy(isRunning = false, remainingMillis = 0L) }
                    _timerSeconds.value = 0
                    onTimerFinished()
                    break
                }
                
                _timerUiState.update { it.copy(remainingMillis = remaining) }
                _timerSeconds.value = (remaining / 1000).toInt()
                _isTimerRunning.value = true
                
                delay(500)
            }
        }
    }

    // ===== TIMER CONTROLS =====
    fun startTimer() {
        if (_timerSeconds.value <= 0) return
        
        timerJob?.cancel()
        _isTimerRunning.value = true
        _isTimerCompleted.value = false
        _isPlaying.value = true // Automatically start meditation
        
        endTimeMillis = System.currentTimeMillis() + (_timerSeconds.value * 1000L)
        _selectedMeditation.value?.id?.let { scheduler.schedule(it, endTimeMillis) }

        startLocalCountdown()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        val remaining = endTimeMillis - System.currentTimeMillis()
        _timerUiState.update { it.copy(isRunning = false, remainingMillis = maxOf(0L, remaining)) }
        _selectedMeditation.value?.id?.let { scheduler.cancel(it) }
        _isTimerRunning.value = false
        _isPlaying.value = false
    }

    fun resumeTimer() {
        val remaining = _timerUiState.value.remainingMillis
        if (remaining > 0) {
            endTimeMillis = System.currentTimeMillis() + remaining
            _selectedMeditation.value?.id?.let { scheduler.schedule(it, endTimeMillis) }
            _timerUiState.update { it.copy(isRunning = true) }
            _isTimerRunning.value = true
            _isPlaying.value = true
            startLocalCountdown()
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _selectedMeditation.value?.id?.let { scheduler.cancel(it) }
        _timerUiState.value = TimerUiState()
        _isTimerRunning.value = false
        _timerSeconds.value = 0
        _isTimerCompleted.value = false
        _isPlaying.value = false
    }

    private fun onTimerFinished() {
        _isTimerRunning.value = false
        _isTimerCompleted.value = true
        _isPlaying.value = false // Stop meditation
        
        _timerUiState.update { it.copy(isRunning = false, remainingMillis = 0) }

        // Call complete session API if a meditation is selected
        val id = _selectedMeditation.value?.id
        val minutes = _timerUiState.value.selectedMinutes
        if (id != null) {
            completeSession(id, minutes, {
                fetchHistory()
            })
        }
    }

    fun togglePlayback() {
        _isPlaying.value = !_isPlaying.value
        // Optionally sync timer with playback
        if (_isTimerRunning.value && !_isPlaying.value) {
            pauseTimer()
        } else if (!_isTimerRunning.value && _isPlaying.value && _timerSeconds.value > 0) {
            startTimer()
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
    fun startDownload(context: Context, id: Long?, title: String?) {
        if (id == null) {
            _errorMessage.value = "Invalid meditation ID"
            return
        }

        if (_downloadState.value == DownloadUiState.Downloading) return

        viewModelScope.launch {
            _downloadState.value = DownloadUiState.Downloading
            repository.getDownloadUrl(id).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val downloadUrl = result.data.downloadUrl
                        if (downloadUrl.isNotEmpty()) {
                            performSystemDownload(context, downloadUrl, id, title ?: "Meditation")
                            _downloadState.value = DownloadUiState.Success
                        } else {
                            _downloadState.value = DownloadUiState.Error("Empty download URL")
                        }
                    }
                    is NetworkResult.Error -> {
                        _downloadState.value = DownloadUiState.Error(result.message ?: "Failed to get download URL")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun performSystemDownload(context: Context, url: String, id: Long, title: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription("Downloading meditation...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "meditation_$id.mp3")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            _downloadState.value = DownloadUiState.Error("Download system error: ${e.message}")
        }
    }

    fun resetDownloadState() {
        _downloadState.value = DownloadUiState.Idle
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

    // ===== ALL MEDITATIONS =====
    fun fetchAllMeditations(category: String? = null) {
        viewModelScope.launch {
            repository.getAllMeditations(category).collect { result ->
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
        resetTimer() // Reset timer for new selection
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

