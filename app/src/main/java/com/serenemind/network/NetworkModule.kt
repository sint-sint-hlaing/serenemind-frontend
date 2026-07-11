package com.serenemind.network

import android.content.Context
import com.serenemind.datastore.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "http://192.168.1.11:8080/"
    private const val BASE_URL = "http://192.168.1.9:8080/"

    fun provideOkHttpClient(context: Context): OkHttpClient {
        val tokenManager = TokenManager(context)

        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { tokenManager.getToken() }
            val requestBuilder = chain.request().newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    fun provideRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideApiService(context: Context): ApiService {
        return provideRetrofit(context).create(ApiService::class.java)
    }

    fun provideGoalApiService(context: Context): GoalApiService{
        return provideRetrofit(context).create(GoalApiService::class.java)
    }

    fun provideMeditationApiService(context: Context): MeditationApiService {
        return provideRetrofit(context).create(MeditationApiService::class.java)
    }
}
