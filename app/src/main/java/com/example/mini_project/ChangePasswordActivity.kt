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


        // BACK

        btnBack.setOnClickListener {
            finish()
        }


        // CHANGE PASSWORD

        btnChangePassword.setOnClickListener {
            changePassword()
        }
    }


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


        // VALIDATION

        if (currentPassword.isEmpty()) {

            edtCurrentPassword.error =
                "Enter your current password"

            return
        }

        if (newPassword.isEmpty()) {

            edtNewPassword.error =
                "Enter a new password"

            return
        }

        if (newPassword.length < 6) {

            edtNewPassword.error =
                "Password must be at least 6 characters"

            return
        }

        if (confirmPassword.isEmpty()) {

            edtConfirmPassword.error =
                "Confirm your new password"

            return
        }

        if (newPassword != confirmPassword) {

            edtConfirmPassword.error =
                "Passwords do not match"

            return
        }

        if (currentPassword == newPassword) {

            edtNewPassword.error =
                "New password must be different"

            return
        }


        val userId =
            Auth.getUserId(this)

        if (userId == -1) {

            Toast.makeText(
                this,
                "User not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val request =
            ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )


        btnChangePassword.isEnabled = false


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

                            Toast.makeText(
                                this@ChangePasswordActivity,
                                "Password changed successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            when (response.code()) {

                                400 -> {
                                    Toast.makeText(
                                        this@ChangePasswordActivity,
                                        "Current password is incorrect",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                404 -> {
                                    Toast.makeText(
                                        this@ChangePasswordActivity,
                                        "User not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    Toast.makeText(
                                        this@ChangePasswordActivity,
                                        "Failed to change password: ${response.code()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<ChangePasswordResponse>,
                        t: Throwable
                    ) {

                        btnChangePassword.isEnabled = true

                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Connection error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }
}