package com.serenemind.repository

import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.request.TimerRequest
import com.serenemind.model.response.*
import com.serenemind.network.MeditationApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow

class MeditationRepository(private val apiService: MeditationApiService) : SafeApiCall() {
    // ===== DASHBOARD =====
    fun getDashboard(): Flow<NetworkResult<MeditationDashboardResponse>> = safeApiCall {
        apiService.getDashboard()
    }

    // ===== ALL MEDITATIONS =====
    fun getAllMeditations(): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.getAllMeditations()
    }

    // ===== GET BY ID =====
    fun getMeditationById(id: Long): Flow<NetworkResult<MeditationResponse>> = safeApiCall {
        apiService.getMeditationById(id)
    }

    // ===== GET BY CATEGORY =====
    fun getByCategory(category: String): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.getByCategory(category)
    }

    // ===== GET BY TIME =====
    fun getByTime(time: String): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.getByTime(time)
    }

    // ===== SEARCH =====
    fun searchMeditations(
        query: String? = null,
        category: String? = null,
        time: String? = null
    ): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.searchMeditations(query, category, time)
    }


    // ===== COMPLETE SESSION =====
    fun completeSession(request: MeditationSessionRequest): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.completeSession(request)
    }

    // ===== HISTORY =====
    fun getHistory(): Flow<NetworkResult<List<MeditationHistoryResponse>>> = safeApiCall {
        apiService.getHistory()
    }

    // ===== FAVORITE =====
    fun toggleFavorite(id: Long): Flow<NetworkResult<FavoriteResponse>> = safeApiCall {
        apiService.toggleFavorite(id)
    }

    fun addFavorite(meditationId: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.addFavorite(com.serenemind.model.request.FavoriteRequest(meditationId))
    }

    // ===== TIMER =====
    fun saveTimer(id: Long, minutes: Int): Flow<NetworkResult<TimerResponse>> = safeApiCall {
        apiService.saveTimer(id, TimerRequest(minutes))
    }

    // ===== SHARE =====
    fun getShareLink(id: Long): Flow<NetworkResult<ShareResponse>> = safeApiCall {
        apiService.getShareLink(id)
    }

    // ===== RECOMMENDATIONS =====
    fun getRecommendations(): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.getRecommendations()
    }

    // ===== CONTINUE LISTENING =====
    fun getContinueListening(): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.getContinueListening()
    }

    // ===== PREVIOUS =====
    fun getPrevious(id: Long): Flow<NetworkResult<MeditationResponse>> = safeApiCall {
        apiService.getPrevious(id)
    }

    // ===== NEXT =====
    fun getNext(id: Long): Flow<NetworkResult<MeditationList>> = safeApiCall {
        apiService.getNext(id)
    }

    // ===== DOWNLOAD URL =====
    fun getDownloadUrl(id: Long): Flow<NetworkResult<DownloadUrlResponse>> = safeApiCall {
        apiService.getDownloadUrl(id)
    }

    // ===== STREAM =====
    fun streamAudio(id: Long): Flow<NetworkResult<okhttp3.ResponseBody>> = safeApiCall {
        apiService.streamAudio(id)
    }

    // ===== DOWNLOAD =====
    fun downloadAudio(id: Long): Flow<NetworkResult<okhttp3.ResponseBody>> = safeApiCall {
        apiService.downloadAudio(id)
    }

}
