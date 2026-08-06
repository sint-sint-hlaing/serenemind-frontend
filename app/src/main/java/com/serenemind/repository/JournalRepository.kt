package com.serenemind.repository

import com.serenemind.model.request.JournalRequest
import com.serenemind.model.response.JournalAnalysisResponse
import com.serenemind.model.response.JournalResponse
import com.serenemind.network.JournalApiService
import okhttp3.MultipartBody
import retrofit2.Response

class JournalRepository(
    private val apiService: JournalApiService
) {

    suspend fun createJournal(request: JournalRequest): Response<JournalResponse> {
        return apiService.createJournal(request)
    }

    suspend fun uploadPhoto(id: Int, photo: MultipartBody.Part): Response<Unit> {
        return apiService.uploadPhoto(id, photo)
    }

    suspend fun listJournals(filter: String = "all"): Response<List<JournalResponse>> {
        return apiService.listJournals(filter)
    }

    suspend fun searchJournals(query: String): Response<List<JournalResponse>> {
        return apiService.searchJournals(query)
    }

    suspend fun getJournal(id: Int): Response<JournalResponse> {
        return apiService.getJournal(id)
    }

    suspend fun updateJournal(id: Int, request: JournalRequest): Response<JournalResponse> {
        return apiService.updateJournal(id, request)
    }

    suspend fun deleteJournal(id: Int): Response<Unit> {
        return apiService.deleteJournal(id)
    }

    suspend fun toggleFavorite(id: Int): Response<Unit> {
        return apiService.toggleFavorite(id)
    }

    suspend fun togglePrivate(id: Int): Response<Unit> {
        return apiService.togglePrivate(id)
    }

    suspend fun triggerAnalysis(id: Int): Response<Unit> {
        return apiService.triggerAnalysis(id)
    }

    suspend fun getAnalysis(id: Int): Response<JournalAnalysisResponse> {
        return apiService.getAnalysis(id)
    }

    suspend fun deletePhoto(id: Int): Response<Unit> {
        return apiService.deletePhoto(id)
    }
}
