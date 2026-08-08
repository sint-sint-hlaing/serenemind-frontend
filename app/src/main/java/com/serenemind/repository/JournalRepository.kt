package com.serenemind.repository

import com.serenemind.model.request.JournalRequest
import com.serenemind.model.response.JournalAnalysisResponse
import com.serenemind.model.response.JournalResponse
import com.serenemind.network.JournalApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

class JournalRepository(
    private val apiService: JournalApiService
) : SafeApiCall() {

    fun createJournal(request: JournalRequest): Flow<NetworkResult<JournalResponse>> = safeApiCall {
        apiService.createJournal(request)
    }

    fun uploadPhoto(id: Int, photo: MultipartBody.Part): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.uploadPhoto(id, photo)
    }

    fun listJournals(filter: String = "all"): Flow<NetworkResult<List<JournalResponse>>> = safeApiCall {
        apiService.listJournals(filter)
    }

    fun searchJournals(query: String): Flow<NetworkResult<List<JournalResponse>>> = safeApiCall {
        apiService.searchJournals(query)
    }

    fun getJournal(id: Int): Flow<NetworkResult<JournalResponse>> = safeApiCall {
        apiService.getJournal(id)
    }

    fun updateJournal(id: Int, request: JournalRequest): Flow<NetworkResult<JournalResponse>> = safeApiCall {
        apiService.updateJournal(id, request)
    }

    fun deleteJournal(id: Int): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.deleteJournal(id)
    }

    fun toggleFavorite(id: Int): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.toggleFavorite(id)
    }

    fun togglePrivate(id: Int): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.togglePrivate(id)
    }

    fun triggerAnalysis(id: Int): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.triggerAnalysis(id)
    }

    fun getAnalysis(id: Int): Flow<NetworkResult<JournalAnalysisResponse>> = safeApiCall {
        apiService.getAnalysis(id)
    }

    fun deletePhoto(id: Int): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.deletePhoto(id)
    }
}
