package com.serenemind.network

import com.serenemind.datastore.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath
        
        // Skip auth header for login, register, and refresh
        if (path.contains("api/auth/login") || 
            path.contains("api/auth/register") || 
            path.contains("api/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { tokenManager.getToken() }
        val requestBuilder = originalRequest.newBuilder()
        
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
