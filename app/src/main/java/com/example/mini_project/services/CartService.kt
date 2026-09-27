package com.example.mini_project.backend

import retrofit2.Call

class CartService(
    private val apiService: ApiService
) {

    fun addToCart(
        userId: Int,
        albumId: Int,
        quantity: Int
    ): Call<CartDTO> {

        val request =
            AddToCartRequest(
                userId = userId,
                albumId = albumId,
                quantity = quantity
            )

        return apiService.addToCart(request)
    }

    fun getCarts(): Call<List<CartDTO>> {
        return apiService.getCarts()
    }

    fun getCartByUserId(
        userId: Int
    ): Call<CartDTO> {

        return apiService.getCartByUserId(
            userId
        )
    }

    fun deleteCartItem(
        itemId: Int
    ): Call<Void> {

        return apiService.deleteCartItem(
            itemId
        )
    }

    fun updateCartItem(
        itemId: Int,
        quantity: Int
    ): Call<CartItemDTO> {

        return apiService.updateCartItem(
            itemId,
            UpdateCartItemRequestDTO(
                quantity
            )
        )
    }
}