package com.example.mini_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project.backend.Auth
import com.example.mini_project.backend.GoogleLoginRequest
import com.example.mini_project.backend.GoogleSignInManager
import com.example.mini_project.backend.LoginRequest
import com.example.mini_project.backend.LoginResponse
import com.example.mini_project.instance.RetrofitInstance
import com.google.android.gms.auth.api.signin.GoogleSignIn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogle: Button
    private lateinit var txtRegister: TextView


    // ============================================================
    // GOOGLE SIGN IN
    // ============================================================

    private val googleSignInLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val task =
                GoogleSignIn
                    .getSignedInAccountFromIntent(
                        result.data
                    )

            try {

                val account =
                    task.getResult(
                        com.google.android.gms.common.api.ApiException::class.java
                    )

                val idToken =
                    account.idToken

                if (idToken.isNullOrBlank()) {

                    showToast(
                        "Unable to sign in with Google."
                    )

                    btnGoogle.isEnabled = true

                    return@registerForActivityResult
                }

                loginWithGoogle(idToken)

            } catch (e: Exception) {

                btnGoogle.isEnabled = true

                showToast(
                    "Google Sign-In was cancelled or could not be completed."
                )
            }
        }


    // ============================================================
    // ON CREATE
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


        edtEmail =
            findViewById(R.id.emailInput)

        edtPassword =
            findViewById(R.id.passwordInput)

        btnLogin =
            findViewById(R.id.loginButton)

        txtRegister =
            findViewById(R.id.goToRegister)

        btnGoogle =
            findViewById(R.id.googleButton)


        // ========================================================
        // NORMAL LOGIN
        // ========================================================

        btnLogin.setOnClickListener {
            login()
        }


        // ========================================================
        // REGISTER
        // ========================================================

        txtRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }


        // ========================================================
        // GOOGLE LOGIN
        // ========================================================

        btnGoogle.setOnClickListener {

            btnGoogle.isEnabled = false

            val googleClient =
                GoogleSignInManager.getClient(this)


            // Clear previous Google account session
            googleClient
                .signOut()
                .addOnCompleteListener {

                    // Open Google account chooser
                    googleSignInLauncher.launch(
                        googleClient.signInIntent
                    )
                }
        }
    }


    // ============================================================
    // NORMAL LOGIN
    // ============================================================

    private fun login() {

        val email =
            edtEmail.text
                .toString()
                .trim()

        val password =
            edtPassword.text
                .toString()
                .trim()


        // ========================================================
        // VALIDATION
        // ========================================================

        if (email.isEmpty()) {

            edtEmail.error =
                "Enter your email"

            edtEmail.requestFocus()

            return
        }


        if (password.isEmpty()) {

            edtPassword.error =
                "Enter your password"

            edtPassword.requestFocus()

            return
        }


        btnLogin.isEnabled = false


        val request =
            LoginRequest(
                email = email,
                password = password
            )


        // ========================================================
        // API LOGIN
        // ========================================================

        RetrofitInstance
            .getApi(this)
            .login(request)
            .enqueue(
                object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {

                        btnLogin.isEnabled = true


                        if (response.isSuccessful) {

                            val loginResponse =
                                response.body()


                            if (loginResponse == null) {

                                showToast(
                                    "Unable to complete login. Please try again."
                                )

                                return
                            }


                            saveLoginSession(
                                loginResponse
                            )

                        } else {

                            when (response.code()) {

                                400,
                                401 -> {

                                    showToast(
                                        "Incorrect email or password."
                                    )
                                }

                                404 -> {

                                    showToast(
                                        "Account not found."
                                    )
                                }

                                else -> {

                                    showToast(
                                        "Unable to log in. Please try again."
                                    )
                                }
                            }
                        }
                    }


                    override fun onFailure(
                        call: Call<LoginResponse>,
                        t: Throwable
                    ) {

                        btnLogin.isEnabled = true

                        showToast(
                            "Unable to connect to the server."
                        )
                    }
                }
            )
    }


    // ============================================================
    // GOOGLE LOGIN
    // ============================================================

    private fun loginWithGoogle(
        idToken: String
    ) {

        btnGoogle.isEnabled = false


        val request =
            GoogleLoginRequest(
                idToken = idToken
            )


        RetrofitInstance
            .getApi(this)
            .googleLogin(request)
            .enqueue(
                object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {

                        btnGoogle.isEnabled = true


                        if (response.isSuccessful) {

                            val loginResponse =
                                response.body()


                            if (loginResponse == null) {

                                showToast(
                                    "Unable to complete Google login. Please try again."
                                )

                                return
                            }


                            saveLoginSession(
                                loginResponse
                            )

                        } else {

                            when (response.code()) {

                                400,
                                401 -> {

                                    showToast(
                                        "Google authentication failed."
                                    )
                                }

                                403 -> {

                                    showToast(
                                        "Google account could not be authenticated."
                                    )
                                }

                                else -> {

                                    showToast(
                                        "Unable to log in with Google. Please try again."
                                    )
                                }
                            }
                        }
                    }


                    override fun onFailure(
                        call: Call<LoginResponse>,
                        t: Throwable
                    ) {

                        btnGoogle.isEnabled = true

                        showToast(
                            "Unable to connect to the server."
                        )
                    }
                }
            )
    }


    // ============================================================
    // SAVE LOGIN SESSION
    // ============================================================

    private fun saveLoginSession(
        loginResponse: LoginResponse
    ) {

        Auth.setToken(
            this,
            loginResponse.token
        )


        Auth.setUserId(
            this,
            loginResponse.userId
        )


        val intent =
            Intent(
                this,
                MainActivity::class.java
            )


        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK


        startActivity(intent)

        finish()
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