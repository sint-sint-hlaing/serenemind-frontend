package com.serenemind.repository

import com.serenemind.model.request.JournalRequest
import com.serenemind.model.response.*
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.Response

class JournalRepository(private val apiService: ApiService) {

    fun getAllJournals(filter: String = "all") = flow {
        try {
            emit(apiService.getAllJournals(filter))
        } catch (e: Exception) {
            emit(Response.error<List<JournalResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getJournalById(id: Long) = flow {
        try {
            emit(apiService.getJournalById(id))
        } catch (e: Exception) {
            emit(Response.error<JournalResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun createJournal(request: JournalRequest): Response<JournalResponse> {
        return try {
            apiService.createJournal(request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun updateJournal(id: Long, request: JournalRequest): Response<JournalResponse> {
        return try {
            apiService.updateJournal(id, request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun deleteJournal(id: Long): Response<Unit> {
        return try {
            apiService.deleteJournal(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun toggleFavorite(id: Long): Response<JournalResponse> {
        return try {
            apiService.toggleFavoriteJournal(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun uploadPhoto(id: Long, photo: MultipartBody.Part): Response<JournalPhotoResponse> {
        return try {
            apiService.uploadJournalPhoto(id, photo)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    fun getAnalysis(id: Long) = flow {
        try {
            emit(apiService.getJournalAnalysis(id))
        } catch (e: Exception) {
            emit(Response.error<JournalAnalysisResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }
}
