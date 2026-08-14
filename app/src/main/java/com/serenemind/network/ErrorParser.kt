package com.serenemind.network

import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorParser {

    fun parseError(response: Response<*>, gson: Gson): NetworkResult.Error {
        val errorBody = response.errorBody()?.string()
        
        return try {
            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
            NetworkResult.Error(
                message = errorResponse?.getDisplayMessage() ?: getFallbackMessage(response.code()),
                code = response.code(),
                fieldErrors = errorResponse?.errors
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                message = getFallbackMessage(response.code()),
                code = response.code()
            )
        }
    }

    fun handleException(throwable: Throwable): NetworkResult.Error {
        return when (throwable) {
            is UnknownHostException -> NetworkResult.Error("No internet connection. Please check your network.")
            is SocketTimeoutException -> NetworkResult.Error("Server timed out. Please try again later.")
            is IOException -> NetworkResult.Error("Network error occurred. Please try again.")
            else -> NetworkResult.Error(throwable.localizedMessage ?: "An unexpected error occurred.")
        }
    }

    private fun getFallbackMessage(code: Int): String {
        return when (code) {
            400 -> "Bad Request: The server could not understand the request."
            401 -> "Session Expired: Please log in again."
            403 -> "Access Denied: You don't have permission to perform this action."
            404 -> "Not Found: The requested resource does not exist."
            500 -> "Server Error: Something went wrong on our side. Please try again later."
            else -> "An error occurred (HTTP $code)."
        }
    }
}
