package com.serenemind.network

import android.content.Context
import android.util.Log
import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator(
    private val context: Context,
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "401 Unauthorized detected. Attempting to refresh token...")
        
        // 1. Get current tokens from DataStore
        val accessToken = runBlocking { tokenManager.getToken() }
        val refreshToken = runBlocking { tokenManager.getRefreshToken() }

        if (refreshToken == null) {
            Log.e("TokenAuthenticator", "Refresh token is null, cannot refresh.")
            return null
        }

        synchronized(this) {
            // Check if token was already refreshed by another thread
            val latestToken = runBlocking { tokenManager.getToken() }
            
            val tokenToUse = if (latestToken != accessToken && latestToken != null) {
                Log.d("TokenAuthenticator", "Token was already refreshed by another request.")
                latestToken
            } else {
                Log.d("TokenAuthenticator", "Refreshing token using refreshToken: $refreshToken")
                // We need to refresh it
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkModule.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(ApiService::class.java)
                
                val refreshResponse = runBlocking {
                    try {
                        service.refreshToken(RefreshRequest(refreshToken))
                    } catch (e: Exception) {
                        Log.e("TokenAuthenticator", "Refresh call failed: ${e.message}")
                        null
                    }
                }

                if (refreshResponse != null && refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val newTokens = refreshResponse.body()!!
                    Log.d("TokenAuthenticator", "Token refreshed successfully.")
                    runBlocking {
                        tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    }
                    newTokens.accessToken
                } else {
                    Log.e("TokenAuthenticator", "Refresh failed or invalid response. Clearing tokens.")
                    runBlocking { tokenManager.clearTokens() }
                    null
                }
            }

            return if (tokenToUse != null) {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $tokenToUse")
                    .build()
            } else {
                null
            }
        }
    }
}