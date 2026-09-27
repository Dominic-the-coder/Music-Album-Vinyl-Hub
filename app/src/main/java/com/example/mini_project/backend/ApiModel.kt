package com.example.mini_project.backend

data class UserDTO(
    val id: Int,
    val name: String,
    val email: String,
    val imageId: Int? = null
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val id: Int,
    val name: String,
    val email: String,
    val message: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val userId: Int,
    val token: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val message: String
)

data class GoogleLoginRequest(
    val idToken: String
)

data class AlbumDTO(
    val id: Int,
    val jamendoId: String,
    val title: String,
    val artist: String,
    val imageUrl: String?,
    val releaseDate: String?,
    val price: Double,
    val genre: String?,
    val songs: List<SongDTO> = emptyList()
)

data class SongDTO(
    val id: Int,
    val jamendoId: String,
    val title: String,
    val artist: String,
    val audioUrl: String?,
    val imageUrl: String?,
    val duration: Int?,
    val albumId: Int?,
    val genre: String?
)

data class SongCategoryDTO(
    val id: Int,
    val name: String
)

data class CartDTO(
    val id: Int,
    val userId: Int,
    val items: List<CartItemDTO> = emptyList()
)

data class CartItemDTO(
    val id: Int,
    val albumId: Int,
    val quantity: Int,
    val price: Double
)

data class UpdateCartItemRequestDTO(
    val quantity: Int
)

data class AddToCartRequest(
    val userId: Int,
    val albumId: Int,
    val quantity: Int
)

data class OrderDTO(
    val id: Int,
    val userId: Int,
    val totalAmount: Double,
    val status: String,
    val createdAt: String,
    val items: List<OrderItemDTO>
)

data class OrderItemDTO(
    val id: Int,
    val albumId: Int,
    val albumTitle: String,
    val price: Double,
    val quantity: Int
)

data class PaymentDTO(
    val id: Int,
    val cartId: Int,
    val paymentType: String,
    val paymentStatus: String,
    val stripeSessionId: String?
)

data class PaymentStatusDTO(
    val paymentStatus: String,
    val orderId: Int? = null
)

data class PaymentRequest(
    val cartId: Int
)

data class PaymentResponse(
    val checkoutUrl: String
)

data class ImageDTO(
    val id: Int,
    val name: String?,
    val type: String?
)

data class JamendoAlbumDTO(
    val id: String?,
    val name: String?,
    val artistName: String?,
    val image: String?,
    val releaseDate: String?,
    val tracks: List<JamendoTrackDTO>
)

data class JamendoTrackDTO(
    val id: String?,
    val name: String?,
    val duration: Int,
    val artistName: String?,
    val albumId: String?,
    val albumName: String?,
    val audio: String?,
    val image: String?
)

data class JamendoResponse<T>(
    val results: List<T>
)