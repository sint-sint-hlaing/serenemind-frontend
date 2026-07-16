package com.serenemind.network

import android.content.Context
import com.serenemind.datastore.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    const val BASE_URL = "http://192.168.91.132:8080/"

    fun provideOkHttpClient(context: Context, tokenManager: TokenManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(context, tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun provideRetrofit(context: Context, tokenManager: TokenManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(context, tokenManager))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideApiService(context: Context, tokenManager: TokenManager): ApiService {
        return provideRetrofit(context, tokenManager).create(ApiService::class.java)
    }

    fun provideGoalApiService(context: Context, tokenManager: TokenManager): GoalApiService {
        return provideRetrofit(context, tokenManager).create(GoalApiService::class.java)
    }

    fun provideMeditationApiService(context: Context, tokenManager: TokenManager): MeditationApiService {
        return provideRetrofit(context, tokenManager).create(MeditationApiService::class.java)
    }
}
