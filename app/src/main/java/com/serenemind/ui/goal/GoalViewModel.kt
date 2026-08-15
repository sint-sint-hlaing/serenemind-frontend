// GoalViewModel.kt
package com.serenemind.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.entity.enums.Frequency
import com.serenemind.model.request.GoalRequest
import com.serenemind.model.response.GoalResponse
import com.serenemind.model.response.toUserGoal  // ✅ Import extension function
import com.serenemind.network.NetworkResult
import com.serenemind.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoalViewModel(private val repository: GoalRepository) : ViewModel() {

    // ===== UI State =====
    private val _uiState = MutableStateFlow<GoalUiState>(GoalUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // ===== Selected Goal =====
    private val _selectedGoal = MutableStateFlow<com.serenemind.model.response.UserGoal?>(null)
    val selectedGoal = _selectedGoal.asStateFlow()

    // ===== Create Goal Success =====
    private val _createGoalSuccess = MutableStateFlow(false)
    val createGoalSuccess = _createGoalSuccess.asStateFlow()

    private val _notes = MutableStateFlow<List<com.serenemind.model.response.GoalNoteResponse>>(emptyList())
    val notes = _notes.asStateFlow()

    init {
        fetchGoals()
    }

    // ===== FETCH GOALS =====
    fun fetchGoals() {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            repository.getAllGoals().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = GoalUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        // ✅ Keep as GoalResponse list (no mapping needed)
                        _uiState.value = GoalUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = GoalUiState.Error(result.message ?: "Unknown error occurred")
                    }
                }
            }
        }
    }

    // ===== SELECT GOAL =====
    fun selectGoal(goal: GoalResponse) {
        // ✅ Convert GoalResponse to UserGoal for display
        _selectedGoal.value = goal.toUserGoal()
    }

    fun selectUserGoal(goal: com.serenemind.model.response.UserGoal) {
        _selectedGoal.value = goal
    }

    // ===== UPDATE PROGRESS =====
    fun updateProgress(id: Long, completed: Boolean, note: String?, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            repository.updateProgress(id, completed, note).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = GoalUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        val updatedGoal = result.data
                        fetchGoals()
                        if (_selectedGoal.value?.id == id) {
                            _selectedGoal.value = updatedGoal.toUserGoal()
                        }
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = GoalUiState.Error(result.message ?: "Failed to update progress")
                    }
                }
            }
        }
    }

    // ===== CREATE & UPDATE =====
    fun createGoal(
        title: String,
        description: String,
        targetDays: Int,
        frequency: Frequency = Frequency.DAILY,
        color: String? = null,
        icon: String = "📚",
        startDate: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            _createGoalSuccess.value = false

            val finalStartDate = startDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val request = GoalRequest(
                title = title,
                frequency = frequency,
                description = description,
                targetDays = targetDays,
                unit = "days",
                startDate = finalStartDate,
                icon = icon,
                color = color,
                silentMode = false
            )

            repository.createGoal(request).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = GoalUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        _createGoalSuccess.value = true
                        fetchGoals() // Refresh list
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = GoalUiState.Error(result.message ?: "Failed to create goal")
                    }
                }
            }
        }
    }

    fun updateGoal(
        id: Long,
        title: String,
        description: String,
        targetDays: Int,
        frequency: Frequency,
        color: String? = null,
        icon: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            val request = GoalRequest(
                title = title,
                frequency = frequency,
                description = description,
                targetDays = targetDays,
                color = color,
                icon = icon ?: "📚"
            )
            repository.updateGoal(id, request).collect { result ->
                if (result is NetworkResult.Success) {
                    fetchGoals()
                    if (_selectedGoal.value?.id == id) {
                        _selectedGoal.value = result.data.toUserGoal()
                    }
                } else if (result is NetworkResult.Error) {
                    _uiState.value = GoalUiState.Error(result.message ?: "Failed to update goal")
                }
            }
        }
    }

    // ===== COMPLETE GOAL =====
    fun completeGoal(id: Long) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            repository.completeGoal(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = GoalUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        fetchGoals()
                        val updatedGoal = result.data
                        if (_selectedGoal.value?.id == id) {
                            _selectedGoal.value = updatedGoal.toUserGoal()
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = GoalUiState.Error(result.message ?: "Failed to complete goal")
                    }
                }
            }
        }
    }

    // ===== PAUSE GOAL =====
    fun pauseGoal(id: Long) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            repository.pauseGoal(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = GoalUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        fetchGoals()
                        val updatedGoal = result.data
                        if (_selectedGoal.value?.id == id) {
                            _selectedGoal.value = updatedGoal.toUserGoal()
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = GoalUiState.Error(result.message ?: "Failed to pause goal")
                    }
                }
            }
        }
    }

    // ===== RESUME GOAL =====
    fun resumeGoal(id: Long) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            repository.resumeGoal(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = GoalUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        fetchGoals()
                        val updatedGoal = result.data
                        if (_selectedGoal.value?.id == id) {
                            _selectedGoal.value = updatedGoal.toUserGoal()
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = GoalUiState.Error(result.message ?: "Failed to resume goal")
                    }
                }
            }
        }
    }

    // ===== DELETE GOAL =====
    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            repository.deleteGoal(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = GoalUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        fetchGoals()
                        if (_selectedGoal.value?.id == id) {
                            _selectedGoal.value = null
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = GoalUiState.Error(result.message ?: "Failed to delete goal")
                    }
                }
            }
        }
    }

    // ===== RESET =====
    fun resetCreateGoalSuccess() {
        _createGoalSuccess.value = false
    }

    // ===== NOTE MANAGEMENT =====
    fun fetchNotes(goalId: Long) {
        viewModelScope.launch {
            repository.getNotes(goalId).collect { result ->
                if (result is NetworkResult.Success) {
                    _notes.value = result.data
                }
            }
        }
    }

    fun addNote(goalId: Long, content: String) {
        viewModelScope.launch {
            repository.addNote(goalId, content).collect { result ->
                if (result is NetworkResult.Success) {
                    fetchNotes(goalId)
                    // Also refresh goal to get updated notes list in GoalResponse if needed
                    fetchGoalById(goalId)
                }
            }
        }
    }

    fun updateNote(goalId: Long, noteId: Long, content: String) {
        viewModelScope.launch {
            repository.updateNote(noteId, content).collect { result ->
                if (result is NetworkResult.Success) {
                    fetchNotes(goalId)
                }
            }
        }
    }

    fun deleteNote(goalId: Long, noteId: Long) {
        viewModelScope.launch {
            repository.deleteNote(noteId).collect { result ->
                if (result is NetworkResult.Success) {
                    fetchNotes(goalId)
                }
            }
        }
    }

    fun fetchGoalById(id: Long) {
        viewModelScope.launch {
            repository.getGoalById(id).collect { result ->
                if (result is NetworkResult.Success) {
                    _selectedGoal.value = result.data.toUserGoal()
                }
            }
        }
    }
}
