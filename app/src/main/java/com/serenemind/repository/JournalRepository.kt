package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.JournalRequest
import com.serenemind.model.response.JournalAnalysisResponse
import com.serenemind.model.response.JournalResponse
import com.serenemind.network.JournalApiService
import okhttp3.MultipartBody
import retrofit2.Response

class JournalRepository(
    private val apiService: JournalApiService,
    private val tokenManager: TokenManager
) {

    private suspend fun getAuthToken(): String {
        return "Bearer ${tokenManager.getToken() ?: ""}"
    }

    suspend fun createJournal(request: JournalRequest): Response<JournalResponse> {
        return apiService.createJournal(getAuthToken(), request)
    }

    suspend fun uploadPhoto(id: Int, photo: MultipartBody.Part): Response<JournalResponse> {
        return apiService.uploadPhoto(getAuthToken(), id, photo)
    }

    suspend fun listJournals(filter: String = "all"): Response<List<JournalResponse>> {
        return apiService.listJournals(getAuthToken(), filter)
    }

    suspend fun searchJournals(query: String): Response<List<JournalResponse>> {
        return apiService.searchJournals(getAuthToken(), query)
    }

    suspend fun getJournal(id: Int): Response<JournalResponse> {
        return apiService.getJournal(getAuthToken(), id)
    }

    suspend fun updateJournal(id: Int, request: JournalRequest): Response<JournalResponse> {
        return apiService.updateJournal(getAuthToken(), id, request)
    }

    suspend fun deleteJournal(id: Int): Response<Unit> {
        return apiService.deleteJournal(getAuthToken(), id)
    }

    suspend fun toggleFavorite(id: Int): Response<Unit> {
        return apiService.toggleFavorite(getAuthToken(), id)
    }

    suspend fun togglePrivate(id: Int): Response<Unit> {
        return apiService.togglePrivate(getAuthToken(), id)
    }

    suspend fun triggerAnalysis(id: Int): Response<Unit> {
        return apiService.triggerAnalysis(getAuthToken(), id)
    }

    suspend fun getAnalysis(id: Int): Response<JournalAnalysisResponse> {
        return apiService.getAnalysis(getAuthToken(), id)
    }

    suspend fun deletePhoto(id: Int): Response<Unit> {
        return apiService.deletePhoto(getAuthToken(), id)
    }
}
