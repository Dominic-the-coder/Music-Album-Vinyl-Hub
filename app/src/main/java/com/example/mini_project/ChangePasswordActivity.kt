package com.example.mini_project

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project.backend.Auth
import com.example.mini_project.backend.ChangePasswordRequest
import com.example.mini_project.backend.ChangePasswordResponse
import com.example.mini_project.instance.RetrofitInstance
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var edtCurrentPassword: TextInputEditText
    private lateinit var edtNewPassword: TextInputEditText
    private lateinit var edtConfirmPassword: TextInputEditText
    private lateinit var btnChangePassword: MaterialButton
    private lateinit var btnBack: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_change_password)

        edtCurrentPassword =
            findViewById(R.id.edtCurrentPassword)

        edtNewPassword =
            findViewById(R.id.edtNewPassword)

        edtConfirmPassword =
            findViewById(R.id.edtConfirmPassword)

        btnChangePassword =
            findViewById(R.id.btnChangePassword)

        btnBack =
            findViewById(R.id.btnBack)


        // ========================================================
        // BACK
        // ========================================================

        btnBack.setOnClickListener {
            finish()
        }


        // ========================================================
        // CHANGE PASSWORD
        // ========================================================

        btnChangePassword.setOnClickListener {
            changePassword()
        }
    }


    // ============================================================
    // CHANGE PASSWORD
    // ============================================================

    private fun changePassword() {

        val currentPassword =
            edtCurrentPassword.text
                .toString()
                .trim()

        val newPassword =
            edtNewPassword.text
                .toString()
                .trim()

        val confirmPassword =
            edtConfirmPassword.text
                .toString()
                .trim()


        // ========================================================
        // VALIDATION
        // ========================================================

        if (currentPassword.isEmpty()) {

            edtCurrentPassword.error =
                "Enter your current password"

            edtCurrentPassword.requestFocus()

            return
        }


        if (newPassword.isEmpty()) {

            edtNewPassword.error =
                "Enter a new password"

            edtNewPassword.requestFocus()

            return
        }


        if (newPassword.length < 6) {

            edtNewPassword.error =
                "Password must be at least 6 characters"

            edtNewPassword.requestFocus()

            return
        }


        if (confirmPassword.isEmpty()) {

            edtConfirmPassword.error =
                "Confirm your new password"

            edtConfirmPassword.requestFocus()

            return
        }


        if (newPassword != confirmPassword) {

            edtConfirmPassword.error =
                "Passwords do not match"

            edtConfirmPassword.requestFocus()

            return
        }


        if (currentPassword == newPassword) {

            edtNewPassword.error =
                "New password must be different from your current password"

            edtNewPassword.requestFocus()

            return
        }


        // ========================================================
        // GET USER
        // ========================================================

        val userId =
            Auth.getUserId(this)

        if (userId == -1) {

            showToast(
                "Please log in again."
            )

            return
        }


        // ========================================================
        // REQUEST
        // ========================================================

        val request =
            ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )


        // ========================================================
        // DISABLE BUTTON
        // ========================================================

        btnChangePassword.isEnabled = false


        // ========================================================
        // API REQUEST
        // ========================================================

        RetrofitInstance
            .getApi(this)
            .changePassword(
                userId,
                request
            )
            .enqueue(
                object : Callback<ChangePasswordResponse> {

                    override fun onResponse(
                        call: Call<ChangePasswordResponse>,
                        response: Response<ChangePasswordResponse>
                    ) {

                        btnChangePassword.isEnabled = true


                        if (response.isSuccessful) {

                            showToast(
                                "Password changed successfully."
                            )

                            finish()

                            return
                        }


                        when (response.code()) {

                            400 -> {

                                showToast(
                                    "Current password is incorrect."
                                )
                            }


                            401 -> {

                                showToast(
                                    "Your session has expired. Please log in again."
                                )
                            }


                            404 -> {

                                showToast(
                                    "User account could not be found."
                                )
                            }


                            else -> {

                                showToast(
                                    "Unable to change password. Please try again."
                                )
                            }
                        }
                    }


                    override fun onFailure(
                        call: Call<ChangePasswordResponse>,
                        t: Throwable
                    ) {

                        btnChangePassword.isEnabled = true

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