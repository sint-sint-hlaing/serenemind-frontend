package com.serenemind.network


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {


    private const val BASE_URL = "http://192.168.89.211:8080/"
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideApiService(): ApiService {
        return provideRetrofit().create(ApiService::class.java)
    }
    fun provideGoalApiService(): GoalApiService{
        return provideRetrofit().create(GoalApiService::class.java)
    }

    fun provideJournalApiService(): JournalApiService {
        return provideRetrofit().create(JournalApiService::class.java)
    }
}