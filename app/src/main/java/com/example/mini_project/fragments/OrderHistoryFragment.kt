package com.example.mini_project.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.MainActivity
import com.example.mini_project.R
import com.example.mini_project.adapters.OrderAdapter
import com.example.mini_project.backend.AlbumDTO
import com.example.mini_project.backend.Auth
import com.example.mini_project.backend.OrderDTO
import com.example.mini_project.backend.OrderService
import com.example.mini_project.instance.RetrofitInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryFragment :
    Fragment(R.layout.fragment_order_history) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter
    private lateinit var orderService: OrderService

    private val orders =
        mutableListOf<OrderDTO>()

    private var albums =
        emptyList<AlbumDTO>()


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )


        recyclerView =
            view.findViewById(
                R.id.orderRecyclerView
            )


        val btnBack =
            view.findViewById<ImageView>(
                R.id.btnBack
            )


        orderService =
            OrderService(
                RetrofitInstance.getApi(
                    requireContext()
                )
            )


        adapter =
            OrderAdapter(
                orders = orders,
                albums = albums,

                onViewOrder = { order ->

                    val detailFragment =
                        OrderDetailFragment()


                    val bundle =
                        Bundle()


                    bundle.putInt(
                        "orderId",
                        order.id
                    )


                    detailFragment.arguments =
                        bundle


                    requireActivity()
                        .supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragmentContainer,
                            detailFragment
                        )
                        .addToBackStack(null)
                        .commit()
                }
            )


        recyclerView.layoutManager =
            LinearLayoutManager(
                requireContext()
            )


        recyclerView.adapter =
            adapter


        // ========================================================
        // BACK BUTTON
        // ========================================================

        btnBack.setOnClickListener {

            val mainActivity =
                requireActivity() as MainActivity


            val bottomNavContainer =
                requireActivity()
                    .findViewById<View>(
                        R.id.bottomNavContainer
                    )


            val bottomNav =
                bottomNavContainer
                    .findViewById<BottomNavigationView>(
                        R.id.bottomNav
                    )


            if (
                mainActivity.ordersOpenedFromProfile
            ) {

                mainActivity.ordersOpenedFromProfile =
                    false


                bottomNav.selectedItemId =
                    R.id.nav_profile

            } else {

                bottomNav.selectedItemId =
                    R.id.nav_home
            }
        }


        loadAlbums()
        loadOrders()
    }


    // ============================================================
    // LOAD ALBUMS
    // ============================================================

    private fun loadAlbums() {

        val api =
            RetrofitInstance.getApi(
                requireContext()
            )


        api.getAlbums()
            .enqueue(
                object : Callback<List<AlbumDTO>> {

                    override fun onResponse(
                        call: Call<List<AlbumDTO>>,
                        response: Response<List<AlbumDTO>>
                    ) {

                        if (!isAdded) {
                            return
                        }


                        if (response.isSuccessful) {

                            albums =
                                response.body()
                                    ?: emptyList()


                            adapter.updateAlbums(
                                albums
                            )

                        } else {

                            showToast(
                                "Unable to load album information."
                            )
                        }
                    }


                    override fun onFailure(
                        call: Call<List<AlbumDTO>>,
                        t: Throwable
                    ) {

                        if (!isAdded) {
                            return
                        }


                        showToast(
                            "Unable to load album information. Please try again."
                        )
                    }
                }
            )
    }


    // ============================================================
    // LOAD ORDERS
    // ============================================================

    private fun loadOrders() {

        val userId =
            Auth.getUserId(
                requireContext()
            )


        orders.clear()

        if (::adapter.isInitialized) {
            adapter.updateList(
                emptyList()
            )
        }


        if (userId == -1) {

            showToast(
                "Please log in to view your orders."
            )

            return
        }


        orderService
            .getOrdersByUserId(
                userId
            )
            .enqueue(
                object : Callback<List<OrderDTO>> {

                    override fun onResponse(
                        call: Call<List<OrderDTO>>,
                        response: Response<List<OrderDTO>>
                    ) {

                        if (!isAdded) {
                            return
                        }


                        if (response.isSuccessful) {

                            val result =
                                response.body()
                                    ?: emptyList()


                            orders.clear()

                            orders.addAll(
                                result
                            )


                            adapter.updateList(
                                result
                            )


                            if (result.isEmpty()) {

                                showToast(
                                    "You have no orders yet."
                                )
                            }

                        } else {

                            orders.clear()

                            adapter.updateList(
                                emptyList()
                            )


                            showToast(
                                "Unable to load your orders. Please try again."
                            )
                        }
                    }


                    override fun onFailure(
                        call: Call<List<OrderDTO>>,
                        t: Throwable
                    ) {

                        if (!isAdded) {
                            return
                        }


                        orders.clear()

                        adapter.updateList(
                            emptyList()
                        )


                        showToast(
                            "Unable to load your orders. Please try again."
                        )
                    }
                }
            )
    }


    // ============================================================
    // RESUME
    // ============================================================

    override fun onResume() {

        super.onResume()


        if (::adapter.isInitialized) {

            loadOrders()
        }
    }


    // ============================================================
    // TOAST HELPER
    // ============================================================

    private fun showToast(
        message: String
    ) {

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}