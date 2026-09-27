package com.example.mini_project.backend

import retrofit2.Call

class OrderService(
    private val apiService: ApiService
) {

    fun getOrdersByUserId(
        userId: Int
    ): Call<List<OrderDTO>> {
        return apiService.getOrdersByUserId(userId)
    }
}