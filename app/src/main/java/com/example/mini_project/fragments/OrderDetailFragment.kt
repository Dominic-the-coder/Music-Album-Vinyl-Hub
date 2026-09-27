package com.example.mini_project.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.R
import com.example.mini_project.adapters.OrderDetailsAdapter
import com.example.mini_project.backend.OrderDTO
import com.example.mini_project.instance.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailFragment :
    Fragment(R.layout.fragment_order_detail) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderDetailsAdapter

    private lateinit var txtOrderNumber: TextView
    private lateinit var txtOrderStatus: TextView
    private lateinit var txtOrderDate: TextView
    private lateinit var txtOrderTotal: TextView

    private val orderItems =
        mutableListOf<com.example.mini_project.backend.OrderItemDTO>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        val btnBack =
            view.findViewById<ImageView>(
                R.id.btnBack
            )

        txtOrderNumber =
            view.findViewById(
                R.id.txtOrderNumber
            )

        txtOrderStatus =
            view.findViewById(
                R.id.txtOrderStatus
            )

        txtOrderDate =
            view.findViewById(
                R.id.txtOrderDate
            )

        txtOrderTotal =
            view.findViewById(
                R.id.txtOrderTotal
            )

        recyclerView =
            view.findViewById(
                R.id.orderDetailsRecyclerView
            )

        adapter =
            OrderDetailsAdapter(
                orderItems
            )

        recyclerView.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        recyclerView.adapter =
            adapter

        // Back → Order History
        btnBack.setOnClickListener {

            requireActivity()
                .supportFragmentManager
                .popBackStack()
        }

        val orderId =
            arguments?.getInt(
                "orderId",
                -1
            ) ?: -1

        if (orderId == -1) {

            Toast.makeText(
                requireContext(),
                "Invalid order",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        loadOrder(orderId)
    }

    private fun loadOrder(
        orderId: Int
    ) {

        val api =
            RetrofitInstance.getApi(
                requireContext()
            )

        api.getOrderById(orderId)
            .enqueue(
                object : Callback<OrderDTO> {

                    override fun onResponse(
                        call: Call<OrderDTO>,
                        response: Response<OrderDTO>
                    ) {

                        if (response.isSuccessful) {

                            val order =
                                response.body()

                            if (order != null) {

                                displayOrder(
                                    order
                                )
                            }

                        } else {

                            Toast.makeText(
                                requireContext(),
                                "Failed to load order",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<OrderDTO>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            requireContext(),
                            "Network error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    private fun displayOrder(
        order: OrderDTO
    ) {

        txtOrderNumber.text =
            "Order #${order.id}"

        txtOrderStatus.text =
            order.status

        txtOrderDate.text =
            formatDate(
                order.createdAt
            )

        txtOrderTotal.text =
            String.format(
                Locale.US,
                "RM %.2f",
                order.totalAmount
            )

        adapter.updateList(
            order.items
        )
    }

    private fun formatDate(
        dateString: String
    ): String {

        return try {

            val inputFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
                )

            val outputFormat =
                SimpleDateFormat(
                    "d MMMM yyyy",
                    Locale.getDefault()
                )

            val date =
                inputFormat.parse(
                    dateString
                )

            if (date != null) {

                outputFormat.format(
                    date
                )

            } else {

                dateString
            }

        } catch (e: Exception) {

            dateString
        }
    }
}