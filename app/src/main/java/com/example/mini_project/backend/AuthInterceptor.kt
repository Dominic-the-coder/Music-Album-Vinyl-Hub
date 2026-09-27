package com.example.mini_project.backend

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    context: Context
) : Interceptor {

    private val appContext =
        context.applicationContext

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val token =
            Auth.getToken(appContext)

        val requestBuilder =
            chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.header(
                "Authorization",
                "Bearer $token"
            )
        }

        return chain.proceed(
            requestBuilder.build()
        )
    }
}