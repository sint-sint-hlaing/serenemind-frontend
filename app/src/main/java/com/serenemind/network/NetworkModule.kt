package com.serenemind.network

import android.content.Context
import com.serenemind.datastore.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    // REPLACE THIS WITH YOUR PC'S WIFI IP ADDRESS (e.g., 192.168.0.105)
    private const val BASE_URL = "http://192.168.91.132:8080/"

    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null

    private fun getOkHttpClient(context: Context): OkHttpClient {
        return okHttpClient ?: synchronized(this) {
            okHttpClient ?: run {
                val tokenManager = TokenManager(context.applicationContext)
                
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                val authInterceptor = Interceptor { chain ->
                    val token = runBlocking { tokenManager.getToken() }
                    val originalRequest = chain.request()
                    
                    val path = originalRequest.url.encodedPath
                    // Skip auth header for login and register
                    if (path.contains("api/auth/login") || path.contains("api/auth/register")) {
                        return@Interceptor chain.proceed(originalRequest)
                    }

                    val requestBuilder = originalRequest.newBuilder()
                    if (!token.isNullOrEmpty()) {
                        // Ensure we use header() to replace any existing Authorization headers
                        requestBuilder.header("Authorization", "Bearer $token")
                    }
                    
                    chain.proceed(requestBuilder.build())
                }

                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build().also { okHttpClient = it }
            }
        }
    }

    private fun getRetrofit(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: run {
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().also { retrofit = it }
            }
        }
    }

    fun provideApiService(context: Context): ApiService {
        return getRetrofit(context).create(ApiService::class.java)
    }

    fun provideGoalApiService(context: Context): GoalApiService {
        return getRetrofit(context).create(GoalApiService::class.java)
    }

    fun provideMeditationApiService(context: Context): MeditationApiService {
        return getRetrofit(context).create(MeditationApiService::class.java)
    }
}
