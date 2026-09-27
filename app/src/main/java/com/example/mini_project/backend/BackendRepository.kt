package com.example.mini_project.backend

import android.content.Context
import com.example.mini_project.instance.RetrofitInstance
import retrofit2.Call

class BackendRepository(
    context: Context,
    private val apiService: ApiService =
        RetrofitInstance.getApi(context)
) {

    fun getAlbums() =
        apiService.getAlbums()

    fun getCarts() =
        apiService.getCarts()

    fun getImages() =
        apiService.getImages()

    fun getPayments() =
        apiService.getPayments()

    fun getSongCategories() =
        apiService.getSongCategories()

    fun getSongs() =
        apiService.getSongs()

    fun getUserById(userId: Int) =
        apiService.getUserById(userId)

    fun getOrdersByUserId(userId: Int) =
        apiService.getOrdersByUserId(userId)

    fun createCheckoutSession(
        cartId: Int
    ) =
        apiService.createCheckoutSession(
            PaymentRequest(cartId)
        )

//    fun googleLogin(
//        idToken: String
//    ): Call<LoginResponse> {
//
//        return apiService.googleLogin(
//            GoogleLoginRequest(idToken)
//        )
//    }
}