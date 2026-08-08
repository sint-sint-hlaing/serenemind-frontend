package com.serenemind.repository

import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.response.*
import com.serenemind.network.ApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow

class MeditationRepository(private val apiService: ApiService) : SafeApiCall() {

    fun getDashboard(): Flow<NetworkResult<MeditationDashboardResponse>> = safeApiCall {
        apiService.getMeditationDashboard()
    }

    fun getMeditationById(id: Long): Flow<NetworkResult<MeditationResponse>> = safeApiCall {
        apiService.getMeditationById(id)
    }

    fun completeSession(request: MeditationSessionRequest): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.completeMeditationSession(request)
    }

    fun getHistory(): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.getMeditationHistory()
    }

    fun getRecommendations(): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.getMeditationRecommendations()
    }

    fun search(keyword: String): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.searchMeditation(keyword)
    }

    fun addFavorite(meditationId: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.addFavoriteMeditation(com.serenemind.model.request.FavoriteRequest(meditationId))
    }

    fun toggleFavorite(id: Long): Flow<NetworkResult<FavoriteResponse>> = safeApiCall {
        apiService.toggleMeditationFavorite(id)
    }

    fun saveTimer(id: Long, minutes: Int): Flow<NetworkResult<TimerResponse>> = safeApiCall {
        apiService.saveMeditationTimer(id, com.serenemind.model.request.TimerRequest(minutes))
    }

    fun getShareLink(id: Long): Flow<NetworkResult<ShareResponse>> = safeApiCall {
        apiService.shareMeditation(id)
    }

    fun getPrevious(id: Long): Flow<NetworkResult<MeditationResponse>> = safeApiCall {
        apiService.getPreviousMeditation(id)
    }

    fun getNext(id: Long): Flow<NetworkResult<MeditationList>> = safeApiCall {
        apiService.getNextMeditation(id)
    }

    fun download(id: Long): Flow<NetworkResult<okhttp3.ResponseBody>> = safeApiCall {
        apiService.downloadMeditationAudio(id)
    }

    fun stream(id: Long): Flow<NetworkResult<okhttp3.ResponseBody>> = safeApiCall {
        apiService.streamMeditationAudio(id)
    }

    fun getContinueListening(): Flow<NetworkResult<List<MeditationResponse>>> = safeApiCall {
        apiService.getContinueListening()
    }
}
