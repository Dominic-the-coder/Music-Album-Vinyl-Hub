package com.example.mini_project.backend

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // Albums
    @GET("albums/with-songs")
    fun getAlbums(): Call<List<AlbumDTO>>

    // Cart
    @GET("carts")
    fun getCarts(): Call<List<CartDTO>>

    @GET("carts/user/{userId}")
    fun getCartByUserId(
        @Path("userId") userId: Int
    ): Call<CartDTO>

    @PUT("carts/item/{itemId}")
    fun updateCartItem(
        @Path("itemId") itemId: Int,
        @Body request: UpdateCartItemRequestDTO
    ): Call<CartItemDTO>

    @DELETE("carts/item/{itemId}")
    fun deleteCartItem(
        @Path("itemId") itemId: Int
    ): Call<Void>
    @POST("carts")
    fun addToCart(
        @Body request: AddToCartRequest
    ): Call<CartDTO>

    // Songs
    @GET("songs")
    fun getSongs(): Call<List<SongDTO>>

    // Images
    @GET("images")
    fun getImages(): Call<List<ImageDTO>>

    @GET("images/{id}")
    fun getImage(
        @Path("id") id: Int
    ): Call<ResponseBody>

    // Upload images
    @Multipart
    @POST("images/upload")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<ImageDTO>

    @PUT("users/{id}/image/{imageId}")
    fun updateProfileImage(
        @Path("id") id: Int,
        @Path("imageId") imageId: Int
    ): Call<UserDTO>

    // Payments
    @GET("payments")
    fun getPayments(): Call<List<PaymentDTO>>

    // Payment checkout session
    @POST("payments/create-checkout-session")
    fun createCheckoutSession(
        @Body request: PaymentRequest
    ): Call<PaymentResponse>

    @GET("payments/status/{sessionId}")
    fun getPaymentStatus(
        @Path("sessionId") sessionId: String
    ): Call<PaymentStatusDTO>

    // Song categories
    @GET("song-categories")
    fun getSongCategories(): Call<List<SongCategoryDTO>>

    // Users
    @GET("users/{id}")
    fun getUserById(
        @Path("id") id: Int
    ): Call<UserDTO>

    // Edit users
    @PUT("users/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Body user: UserDTO
    ): Call<UserDTO>

    // Change users password
    @PUT("users/{id}/password")
    fun changePassword(
        @Path("id") id: Int,
        @Body request: ChangePasswordRequest
    ): Call<ChangePasswordResponse>

    // Get all orders for a user
    @GET("orders/user/{userId}")
    fun getOrdersByUserId(
        @Path("userId") userId: Int
    ): Call<List<OrderDTO>>

    // Get one order by ID
    @GET("orders/{id}")
    fun getOrderById(
        @Path("id") orderId: Int
    ): Call<OrderDTO>

    // Authentication
    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("users/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @POST("auth/google")
    fun googleLogin(
        @Body request: GoogleLoginRequest
    ): Call<LoginResponse>
}