package com.serenemind.network


import android.content.Context
import com.serenemind.datastore.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    const val BASE_URL = "http://192.168.90.145:8080/"

    fun provideOkHttpClient(context: Context, tokenManager: TokenManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(context, tokenManager))
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
}