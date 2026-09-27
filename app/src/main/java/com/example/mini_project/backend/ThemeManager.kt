package com.example.mini_project.backend

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREF_NAME = "theme_preferences"
    private const val KEY_DARK_MODE = "dark_mode"

    fun applyTheme(context: Context) {

        val preferences =
            context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        if (!preferences.contains(KEY_DARK_MODE)) {

            // First launch: follow the phone's system theme
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )

        } else {

            val darkMode =
                preferences.getBoolean(
                    KEY_DARK_MODE,
                    false
                )

            AppCompatDelegate.setDefaultNightMode(
                if (darkMode) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    fun isDarkMode(context: Context): Boolean {

        val uiMode =
            context.resources.configuration.uiMode

        return (
                uiMode and
                        Configuration.UI_MODE_NIGHT_MASK
                ) == Configuration.UI_MODE_NIGHT_YES
    }

    fun setDarkMode(
        context: Context,
        darkMode: Boolean
    ) {

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putBoolean(
                KEY_DARK_MODE,
                darkMode
            )
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}