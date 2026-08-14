package com.serenemind.network

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

abstract class SafeApiCall {
    
    // Inject or provide Gson instance
    private val gson = Gson()

    fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Flow<NetworkResult<T>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(NetworkResult.Success(body))
                } else if (response.code() == 204 || response.code() == 200) {
                    @Suppress("UNCHECKED_CAST")
                    emit(NetworkResult.Success(Unit as T))
                } else {
                    emit(NetworkResult.Error("Empty response body", response.code()))
                }
            } else {
                emit(ErrorParser.parseError(response, gson))
            }
        } catch (e: Exception) {
            emit(ErrorParser.handleException(e))
        }
    }.flowOn(Dispatchers.IO)
}
