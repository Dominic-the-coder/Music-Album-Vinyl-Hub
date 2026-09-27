package com.example.mini_project

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project.backend.ThemeManager

class SplashActivity : AppCompatActivity() {

    private val splashDuration = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {

        // Apply saved theme BEFORE creating the UI
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            startActivity(intent)
            finish()

        }, splashDuration)
    }
}