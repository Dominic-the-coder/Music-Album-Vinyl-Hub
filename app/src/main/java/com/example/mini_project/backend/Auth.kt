package com.example.mini_project.backend

import android.content.Context

object Auth {

    private const val PREF_NAME = "user_session"

    private const val TOKEN_KEY = "token"
    private const val USER_ID_KEY = "userId"

    fun setToken(
        context: Context,
        token: String
    ) {
        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(TOKEN_KEY, token)
            .apply()
    }

    fun getToken(
        context: Context
    ): String? {
        return context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .getString(TOKEN_KEY, null)
    }

    fun setUserId(
        context: Context,
        userId: Int
    ) {
        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putInt(USER_ID_KEY, userId)
            .apply()
    }

    fun getUserId(
        context: Context
    ): Int {
        return context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .getInt(USER_ID_KEY, -1)
    }

    fun clearToken(
        context: Context
    ) {
        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .remove(TOKEN_KEY)
            .apply()
    }

    fun clearUserId(
        context: Context
    ) {
        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .remove(USER_ID_KEY)
            .apply()
    }

    fun isLoggedIn(
        context: Context
    ): Boolean {
        return !getToken(context).isNullOrBlank() &&
                getUserId(context) != -1
    }

    fun logout(
        context: Context
    ) {
        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .clear()
            .apply()
    }
}