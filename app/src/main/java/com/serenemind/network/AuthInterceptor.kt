package com.serenemind.network

import com.serenemind.datastore.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.getToken() }
        val request = chain.request().newBuilder()
        
        if (token != null) {
            request.header("Authorization", "Bearer $token")
        }
        
        return chain.proceed(request.build())
    }
}