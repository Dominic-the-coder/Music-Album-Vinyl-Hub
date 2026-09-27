package com.example.mini_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project.instance.RetrofitInstance
import com.example.mini_project.backend.PaymentStatusDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent?.data

        if (uri == null) {
            paymentFailed("Invalid payment result")
            return
        }

        val sessionId =
            uri.getQueryParameter("session_id")

        if (sessionId.isNullOrBlank()) {
            paymentFailed("Payment session ID is missing")
            return
        }

        checkPayment(sessionId)
    }

    private fun checkPayment(
        sessionId: String
    ) {

        RetrofitInstance
            .getApi(this)
            .getPaymentStatus(sessionId)
            .enqueue(
                object : Callback<PaymentStatusDTO> {

                    override fun onResponse(
                        call: Call<PaymentStatusDTO>,
                        response: Response<PaymentStatusDTO>
                    ) {

                        if (response.isSuccessful) {

                            val result =
                                response.body()

                            if (result == null) {
                                paymentFailed(
                                    "Empty payment response"
                                )
                                return
                            }

                            if (
                                result.paymentStatus
                                    .equals(
                                        "PAID",
                                        ignoreCase = true
                                    )
                            ) {

                                Toast.makeText(
                                    this@PaymentResultActivity,
                                    "Payment successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                val intent =
                                    Intent(
                                        this@PaymentResultActivity,
                                        MainActivity::class.java
                                    )

                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP

                                intent.putExtra(
                                    "paymentSuccess",
                                    true
                                )

                                startActivity(intent)

                                finish()

                            } else {

                                paymentFailed(
                                    "Payment was not completed"
                                )
                            }

                        } else {

                            paymentFailed(
                                "Payment verification failed: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<PaymentStatusDTO>,
                        t: Throwable
                    ) {

                        paymentFailed(
                            "Payment verification error: ${t.message}"
                        )
                    }
                }
            )
    }

    private fun paymentFailed(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()

        val intent =
            Intent(
                this,
                CartActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(intent)

        finish()
    }
}