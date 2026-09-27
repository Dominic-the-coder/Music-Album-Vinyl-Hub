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


        // ========================================================
        // REGISTER
        // ========================================================

        signupButton.setOnClickListener {

            val name =
                nameInput.text
                    .toString()
                    .trim()

            val email =
                emailInput.text
                    .toString()
                    .trim()

            val password =
                passwordInput.text
                    .toString()

            val confirmPassword =
                confirmPasswordInput.text
                    .toString()


            // ====================================================
            // VALIDATION
            // ====================================================

            if (name.isEmpty()) {

                nameInput.error =
                    "Enter your name"

                nameInput.requestFocus()

                return@setOnClickListener
            }


            if (email.isEmpty()) {

                emailInput.error =
                    "Enter your email"

                emailInput.requestFocus()

                return@setOnClickListener
            }


            if (password.isEmpty()) {

                passwordInput.error =
                    "Enter a password"

                passwordInput.requestFocus()

                return@setOnClickListener
            }


            if (password.length < 6) {

                passwordInput.error =
                    "Password must be at least 6 characters"

                passwordInput.requestFocus()

                return@setOnClickListener
            }


            if (confirmPassword.isEmpty()) {

                confirmPasswordInput.error =
                    "Confirm your password"

                confirmPasswordInput.requestFocus()

                return@setOnClickListener
            }


            if (password != confirmPassword) {

                confirmPasswordInput.error =
                    "Passwords do not match"

                confirmPasswordInput.requestFocus()

                return@setOnClickListener
            }


            register(
                name,
                email,
                password
            )
        }


        // ========================================================
        // GO TO LOGIN
        // ========================================================

        goToLogin.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }


    // ============================================================
    // REGISTER
    // ============================================================

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
            .enqueue(
                object : Callback<RegisterResponse> {

                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {

                        if (response.isSuccessful) {

                            showToast(
                                "Account created successfully."
                            )


                            startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                )
                            )


                            finish()

                        } else {

                            when (response.code()) {

                                400 -> {

                                    showToast(
                                        "Please check your registration details."
                                    )
                                }


                                409 -> {

                                    showToast(
                                        "An account with this email already exists."
                                    )
                                }


                                500 -> {

                                    showToast(
                                        "Unable to create your account. Please try again."
                                    )
                                }


                                else -> {

                                    showToast(
                                        "Unable to create your account. Please try again."
                                    )
                                }
                            }
                        }
                    }


                    override fun onFailure(
                        call: Call<RegisterResponse>,
                        t: Throwable
                    ) {

                        showToast(
                            "Unable to connect to the server."
                        )
                    }
                }
            )
    }


    // ============================================================
    // TOAST HELPER
    // ============================================================

    private fun showToast(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}