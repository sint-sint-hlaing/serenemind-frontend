package com.serenemind.network

import android.content.Context
import com.serenemind.datastore.TokenManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.TimeUnit

object NetworkModule {

    // Use 10.0.2.2 for Android Emulator, or your computer's LAN IP for physical devices
    const val BASE_URL = "http://10.215.79.239:8080/"

    fun provideOkHttpClient(context: Context, tokenManager: TokenManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(context, tokenManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun provideRetrofit(context: Context, tokenManager: TokenManager): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()
            
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(context, tokenManager))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun provideApiService(context: Context, tokenManager: TokenManager): ApiService {
        return provideRetrofit(context, tokenManager).create(ApiService::class.java)
    }

    fun provideGoalApiService(context: Context, tokenManager: TokenManager): GoalApiService {
        return provideRetrofit(context, tokenManager).create(GoalApiService::class.java)
    }
    fun provideMoodApiService(context: Context, tokenManager: TokenManager): MoodApiService {
        return provideRetrofit(context, tokenManager).create(MoodApiService::class.java)
    }


    fun provideJournalApiService(context: Context, tokenManager: TokenManager): JournalApiService {
        return provideRetrofit(context, tokenManager).create(JournalApiService::class.java)
    }

    fun provideMeditationApiService(context: Context, tokenManager: TokenManager): MeditationApiService {
        return provideRetrofit(context, tokenManager).create(MeditationApiService::class.java)
    }

    fun provideChatApiService(context: Context, tokenManager: TokenManager): ChatApiService {
        return provideRetrofit(context, tokenManager).create(ChatApiService::class.java)
    }
}
