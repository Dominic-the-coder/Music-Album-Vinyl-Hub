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

    // =========================
    // GOOGLE SIGN IN
    // =========================

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

                    Toast.makeText(
                        this,
                        "Google ID token is missing",
                        Toast.LENGTH_LONG
                    ).show()

                    return@registerForActivityResult
                }

                loginWithGoogle(idToken)

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    "Google Sign-In failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

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

        // =========================
        // NORMAL LOGIN
        // =========================

        btnLogin.setOnClickListener {
            login()
        }

        // =========================
        // REGISTER
        // =========================

        txtRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }

        // =========================
        // GOOGLE LOGIN
        // =========================

        btnGoogle.setOnClickListener {

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

    // =========================
    // NORMAL LOGIN
    // =========================

    private fun login() {

        val email =
            edtEmail.text.toString().trim()

        val password =
            edtPassword.text.toString().trim()

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

        RetrofitInstance
            .getApi(this)
            .login(request)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    btnLogin.isEnabled = true

                    if (response.isSuccessful) {

                        val loginResponse =
                            response.body()

                        if (loginResponse == null) {

                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid server response",
                                Toast.LENGTH_SHORT
                            ).show()

                            return
                        }

                        saveLoginSession(
                            loginResponse
                        )

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<LoginResponse>,
                    t: Throwable
                ) {

                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this@LoginActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // =========================
    // GOOGLE LOGIN
    // =========================

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
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    btnGoogle.isEnabled = true

                    if (response.isSuccessful) {

                        val loginResponse =
                            response.body()

                        if (loginResponse == null) {

                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid Google login response",
                                Toast.LENGTH_LONG
                            ).show()

                            return
                        }

                        saveLoginSession(
                            loginResponse
                        )

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            "Google login failed: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<LoginResponse>,
                    t: Throwable
                ) {

                    btnGoogle.isEnabled = true

                    Toast.makeText(
                        this@LoginActivity,
                        "Google server error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // =========================
    // SAVE LOGIN SESSION
    // =========================

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
}