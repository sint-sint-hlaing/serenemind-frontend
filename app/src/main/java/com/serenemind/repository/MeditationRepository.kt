package com.serenemind.repository

import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.response.MeditationDashboardResponse
import com.serenemind.model.response.MeditationResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class MeditationRepository(private val apiService: ApiService) {

    fun getDashboard() = flow {
        try {
            emit(apiService.getMeditationDashboard())
        } catch (e: Exception) {
            emit(Response.error<MeditationDashboardResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getMeditationById(id: Long) = flow {
        try {
            emit(apiService.getMeditationById(id))
        } catch (e: Exception) {
            emit(Response.error<MeditationResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun completeSession(request: MeditationSessionRequest): Response<Unit> {
        return try {
            apiService.completeMeditationSession(request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    fun getHistory() = flow {
        try {
            emit(apiService.getMeditationHistory())
        } catch (e: Exception) {
            emit(Response.error<List<MeditationResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getRecommendations() = flow {
        try {
            emit(apiService.getMeditationRecommendations())
        } catch (e: Exception) {
            emit(Response.error<List<MeditationResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun search(keyword: String) = flow {
        try {
            emit(apiService.searchMeditation(keyword))
        } catch (e: Exception) {
            emit(Response.error<List<MeditationResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun addFavorite(meditationId: Long): Response<Unit> {
        return try {
            apiService.addFavoriteMeditation(com.serenemind.model.request.FavoriteRequest(meditationId))
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun toggleFavorite(id: Long): Response<com.serenemind.model.response.FavoriteResponse> {
        return try {
            apiService.toggleMeditationFavorite(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun saveTimer(id: Long, minutes: Int): Response<com.serenemind.model.response.TimerResponse> {
        return try {
            apiService.saveMeditationTimer(id, com.serenemind.model.request.TimerRequest(minutes))
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun getShareLink(id: Long): Response<com.serenemind.model.response.ShareResponse> {
        return try {
            apiService.shareMeditation(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun getPrevious(id: Long): Response<MeditationResponse> {
        return try {
            apiService.getPreviousMeditation(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun getNext(id: Long): Response<com.serenemind.model.response.MeditationList> {
        return try {
            apiService.getNextMeditation(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun download(id: Long): Response<okhttp3.ResponseBody> {
        return try {
            apiService.downloadMeditationAudio(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun stream(id: Long): Response<okhttp3.ResponseBody> {
        return try {
            apiService.streamMeditationAudio(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    fun getContinueListening() = flow {
        try {
            emit(apiService.getContinueListening())
        } catch (e: Exception) {
            emit(Response.error<List<MeditationResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }
}
