package com.serenemind.repository

import com.serenemind.model.request.GoalNoteRequest
import com.serenemind.model.request.GoalRequest
import com.serenemind.model.request.NoteUpdateRequest
import com.serenemind.model.response.GoalNoteResponse
import com.serenemind.model.response.GoalResponse
import com.serenemind.model.response.GoalStatistics
import com.serenemind.model.response.HomeResponse
import com.serenemind.network.GoalApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow



class GoalRepository (private val apiService: GoalApiService) : SafeApiCall() {
    // ===== CREATE & UPDATE =====
    fun createGoal(request: GoalRequest): Flow<NetworkResult<GoalResponse>> = safeApiCall {
        apiService.createGoal(request)
    }

    fun updateGoal(id: Long, request: GoalRequest): Flow<NetworkResult<GoalResponse>> = safeApiCall {
        apiService.updateGoal(id, request)
    }

    // ===== GET ALL GOALS =====
    fun getAllGoals(): Flow<NetworkResult<List<GoalResponse>>> = safeApiCall {
        apiService.getAllGoals()
    }

    // ===== GET GOAL BY ID =====
    fun getGoalById(id: Long): Flow<NetworkResult<GoalResponse>> = safeApiCall {
        apiService.getGoalById(id)
    }

    // ===== GET ACTIVE GOALS =====
    fun getActiveGoals(): Flow<NetworkResult<List<GoalResponse>>> = safeApiCall {
        apiService.getActiveGoals()
    }

    // ===== GET COMPLETED GOALS =====
    fun getCompletedGoals(): Flow<NetworkResult<List<GoalResponse>>> = safeApiCall {
        apiService.getCompletedGoals()
    }

    // ===== GET GOALS BY STATUS =====
    fun getGoalsByStatus(status: String): Flow<NetworkResult<List<GoalResponse>>> = safeApiCall {
        apiService.getGoalsByStatus(status)
    }

    // ===== GET STATISTICS =====
    fun getGoalStatistics(): Flow<NetworkResult<GoalStatistics>> = safeApiCall {
        apiService.getGoalStatistics()
    }

    // ===== UPDATE PROGRESS =====
    fun updateProgress(id: Long, completed: Boolean, note: String?): Flow<NetworkResult<GoalResponse>> = safeApiCall {
        apiService.updateProgress(id, com.serenemind.model.request.GoalProgressRequest(completed, note))
    }

    // ===== COMPLETE GOAL =====
    fun completeGoal(id: Long): Flow<NetworkResult<GoalResponse>> = safeApiCall {
        apiService.completeGoal(id)
    }

    // ===== PAUSE GOAL =====
    fun pauseGoal(id: Long): Flow<NetworkResult<GoalResponse>> = safeApiCall {
        apiService.pauseGoal(id)
    }

    // ===== RESUME GOAL =====
    fun resumeGoal(id: Long): Flow<NetworkResult<GoalResponse>> = safeApiCall {
        apiService.resumeGoal(id)
    }

    // ===== DELETE (Soft Delete) =====
    fun deleteGoal(id: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.deleteGoal(id)
    }

    // ===== HARD DELETE =====
    fun hardDeleteGoal(id: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.hardDeleteGoal(id)
    }

    // ===== NOTE MANAGEMENT =====
    fun addNote(id: Long, content: String): Flow<NetworkResult<GoalNoteResponse>> = safeApiCall {
        apiService.addNote(id, GoalNoteRequest(content))
    }

    fun getNotes(id: Long): Flow<NetworkResult<List<GoalNoteResponse>>> = safeApiCall {
        apiService.getNotes(id)
    }

    fun updateNote(noteId: Long, content: String): Flow<NetworkResult<GoalNoteResponse>> = safeApiCall {
        apiService.updateNote(noteId, NoteUpdateRequest(content))
    }

    fun deleteNote(noteId: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.deleteNote(noteId)
    }

    // ===== UI SPECIFIC =====
    fun getDashboardGoals(): Flow<NetworkResult<List<GoalResponse>>> = safeApiCall {
        apiService.getDashboardGoals()
    }

    fun getHomeData(): Flow<NetworkResult<HomeResponse>> = safeApiCall {
        apiService.getHomeData()
    }

}
