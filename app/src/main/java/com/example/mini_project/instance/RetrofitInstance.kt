package com.example.mini_project.instance

import android.content.Context
import com.example.mini_project.backend.ApiService
import com.example.mini_project.backend.AuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.1.104.43:8080/"

    private var apiService: ApiService? = null

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun getApi(
        context: Context
    ): ApiService {

        if (apiService == null) {

            val client =
                OkHttpClient.Builder()
                    .addInterceptor(
                        AuthInterceptor(
                            context.applicationContext
                        )
                    )
                    .build()

            val retrofit =
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(
                        MoshiConverterFactory.create(moshi)
                    )
                    .build()

            apiService =
                retrofit.create(
                    ApiService::class.java
                )
        }

        return apiService!!
    }
}