package com.example.mini_project

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project.backend.RegisterRequest
import com.example.mini_project.backend.RegisterResponse
import com.example.mini_project.instance.RetrofitInstance
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val nameInput =
            findViewById<TextInputEditText>(
                R.id.nameInput
            )

        val emailInput =
            findViewById<TextInputEditText>(
                R.id.emailInput
            )

        val passwordInput =
            findViewById<TextInputEditText>(
                R.id.passwordInput
            )

        val confirmPasswordInput =
            findViewById<TextInputEditText>(
                R.id.confirmPasswordInput
            )

        val signupButton =
            findViewById<MaterialButton>(
                R.id.registerButton
            )

        val goToLogin =
            findViewById<TextView>(
                R.id.goToLogin
            )

        signupButton.setOnClickListener {

            val name =
                nameInput.text.toString().trim()

            val email =
                emailInput.text.toString().trim()

            val password =
                passwordInput.text.toString()

            val confirmPassword =
                confirmPasswordInput.text.toString()

            if (name.isEmpty()) {
                nameInput.error = "Name is required"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                emailInput.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "Password is required"
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordInput.error =
                    "Password must be at least 6 characters"

                return@setOnClickListener
            }

            if (password != confirmPassword) {

                confirmPasswordInput.error =
                    "Passwords do not match"

                return@setOnClickListener
            }

            register(
                name,
                email,
                password
            )
        }

        goToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register(
        name: String,
        email: String,
        password: String
    ) {

        val request =
            RegisterRequest(
                name = name,
                email = email,
                password = password
            )

        RetrofitInstance
            .getApi(this)
            .register(request)
            .enqueue(object : Callback<RegisterResponse> {

                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {

                    if (response.isSuccessful) {

                        val registerResponse = response.body()

                        Toast.makeText(
                            this@RegisterActivity,
                            registerResponse?.message
                                ?: "Registration successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this@RegisterActivity,
                                LoginActivity::class.java
                            )
                        )

                        finish()

                    } else {

                        val message = when (response.code()) {

                            400 -> "Invalid registration details"

                            409 -> "Email already exists"

                            500 -> "Server error"

                            else ->
                                "Registration failed: ${response.code()}"
                        }

                        Toast.makeText(
                            this@RegisterActivity,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<RegisterResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}