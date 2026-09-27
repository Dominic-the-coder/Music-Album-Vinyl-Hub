package com.example.mini_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.adapters.CartAdapter
import com.example.mini_project.backend.AlbumDTO
import com.example.mini_project.backend.Auth
import com.example.mini_project.backend.CartDTO
import com.example.mini_project.backend.CartItemDTO
import com.example.mini_project.backend.CartService
import com.example.mini_project.backend.PaymentRequest
import com.example.mini_project.backend.PaymentResponse
import com.example.mini_project.instance.RetrofitInstance
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity() {

    private lateinit var adapter: CartAdapter

    private lateinit var txtSubtotal: TextView
    private lateinit var txtShipping: TextView
    private lateinit var txtTotal: TextView

    private lateinit var cartService: CartService

    private val cartItems =
        mutableListOf<CartItemDTO>()

    private var albums =
        emptyList<AlbumDTO>()

    private var cartId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cart)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(android.R.id.content)
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.cartRecyclerView
            )

        txtSubtotal =
            findViewById(R.id.txtSubtotal)

        txtShipping =
            findViewById(R.id.txtShipping)

        txtTotal =
            findViewById(R.id.txtTotal)

        val btnBack =
            findViewById<ImageView>(
                R.id.btnBack
            )

        val btnCheckout =
            findViewById<MaterialButton>(
                R.id.btnCheckout
            )

        cartService =
            CartService(
                RetrofitInstance.getApi(this)
            )


        // ========================================================
        // CART ADAPTER
        // ========================================================

        adapter =
            CartAdapter(
                cartItems,
                albums,

                onDelete = { item ->
                    deleteCartItem(item)
                },

                onChange = { updatedItem ->

                    val index =
                        cartItems.indexOfFirst {
                            it.id == updatedItem.id
                        }

                    if (index != -1) {

                        cartItems[index] =
                            updatedItem
                    }

                    calculateTotal()

                    updateCartItemOnServer(
                        updatedItem
                    )
                }
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            adapter


        // ========================================================
        // BACK BUTTON
        // ========================================================

        btnBack.setOnClickListener {
            finish()
        }


        // ========================================================
        // CHECKOUT
        // ========================================================

        btnCheckout.setOnClickListener {

            if (cartItems.isEmpty()) {

                showToast(
                    "Your cart is empty"
                )

                return@setOnClickListener
            }

            if (cartId == -1) {

                showToast(
                    "Cart is not available. Please try again."
                )

                return@setOnClickListener
            }

            createCheckoutSession()
        }


        loadAlbums()
        loadCart()
    }


    // ============================================================
    // LOAD ALBUMS
    // ============================================================

    private fun loadAlbums() {

        RetrofitInstance
            .getApi(this)
            .getAlbums()
            .enqueue(
                object : Callback<List<AlbumDTO>> {

                    override fun onResponse(
                        call: Call<List<AlbumDTO>>,
                        response: Response<List<AlbumDTO>>
                    ) {

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

                        showToast(
                            "Unable to connect to the server."
                        )
                    }
                }
            )
    }


    // ============================================================
    // LOAD CART
    // ============================================================

    private fun loadCart() {

        val userId =
            Auth.getUserId(this)

        if (userId == -1) {

            showToast(
                "Please log in to view your cart."
            )

            return
        }

        cartService
            .getCartByUserId(userId)
            .enqueue(
                object : Callback<CartDTO> {

                    override fun onResponse(
                        call: Call<CartDTO>,
                        response: Response<CartDTO>
                    ) {

                        if (response.isSuccessful) {

                            val cart =
                                response.body()

                            cartItems.clear()

                            if (cart != null) {

                                cartId =
                                    cart.id

                                cartItems.addAll(
                                    cart.items
                                )

                            } else {

                                cartId = -1
                            }

                            adapter.updateList(
                                cartItems
                            )

                            calculateTotal()

                        } else if (response.code() == 404) {

                            cartId = -1

                            cartItems.clear()

                            adapter.updateList(
                                cartItems
                            )

                            calculateTotal()

                        } else {

                            showToast(
                                "Unable to load your cart. Please try again."
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<CartDTO>,
                        t: Throwable
                    ) {

                        showToast(
                            "Unable to connect to the server."
                        )
                    }
                }
            )
    }


    // ============================================================
    // UPDATE CART ITEM ON SERVER
    // ============================================================

    private fun updateCartItemOnServer(
        item: CartItemDTO
    ) {

        cartService
            .updateCartItem(
                item.id,
                item.quantity
            )
            .enqueue(
                object : Callback<CartItemDTO> {

                    override fun onResponse(
                        call: Call<CartItemDTO>,
                        response: Response<CartItemDTO>
                    ) {

                        if (response.isSuccessful) {

                            val updatedItem =
                                response.body()

                            if (updatedItem != null) {

                                val index =
                                    cartItems.indexOfFirst {
                                        it.id == updatedItem.id
                                    }

                                if (index != -1) {

                                    cartItems[index] =
                                        updatedItem
                                }
                            }

                            calculateTotal()

                        } else {

                            showToast(
                                "Unable to update cart quantity."
                            )

                            loadCart()
                        }
                    }

                    override fun onFailure(
                        call: Call<CartItemDTO>,
                        t: Throwable
                    ) {

                        showToast(
                            "Unable to update cart quantity."
                        )

                        loadCart()
                    }
                }
            )
    }


    // ============================================================
    // CREATE STRIPE CHECKOUT
    // ============================================================

    private fun createCheckoutSession() {

        val request =
            PaymentRequest(
                cartId = cartId
            )

        RetrofitInstance
            .getApi(this)
            .createCheckoutSession(
                request
            )
            .enqueue(
                object : Callback<PaymentResponse> {

                    override fun onResponse(
                        call: Call<PaymentResponse>,
                        response: Response<PaymentResponse>
                    ) {

                        if (!response.isSuccessful) {

                            val error =
                                response.errorBody()
                                    ?.string()

                            showToast(
                                if (!error.isNullOrBlank()) {
                                    "Checkout failed. Please try again."
                                } else {
                                    "Unable to start checkout."
                                }
                            )

                            return
                        }

                        val paymentResponse =
                            response.body()

                        if (paymentResponse == null) {

                            showToast(
                                "Unable to start checkout. Please try again."
                            )

                            return
                        }

                        val checkoutUrl =
                            paymentResponse.checkoutUrl

                        if (checkoutUrl.isBlank()) {

                            showToast(
                                "Payment service is unavailable."
                            )

                            return
                        }

                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(checkoutUrl)
                            )

                        try {

                            startActivity(intent)

                        } catch (e: Exception) {

                            showToast(
                                "Unable to open the payment page."
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<PaymentResponse>,
                        t: Throwable
                    ) {

                        showToast(
                            "Unable to connect to the payment service."
                        )
                    }
                }
            )
    }


    // ============================================================
    // DELETE CART ITEM
    // ============================================================

    private fun deleteCartItem(
        item: CartItemDTO
    ) {

        cartService
            .deleteCartItem(
                item.id
            )
            .enqueue(
                object : Callback<Void> {

                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {

                        if (response.isSuccessful) {

                            cartItems.remove(item)

                            adapter.updateList(
                                cartItems
                            )

                            calculateTotal()

                            showToast(
                                "Item removed from cart."
                            )

                        } else {

                            showToast(
                                "Unable to remove item from cart."
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<Void>,
                        t: Throwable
                    ) {

                        showToast(
                            "Unable to connect to the server."
                        )
                    }
                }
            )
    }


    // ============================================================
    // CALCULATE TOTAL
    // ============================================================

    private fun calculateTotal() {

        val subtotal =
            cartItems.sumOf {
                it.price * it.quantity
            }

        val shipping =
            0.00

        val total =
            subtotal + shipping

        txtSubtotal.text =
            String.format(
                "RM %.2f",
                subtotal
            )

        txtShipping.text =
            String.format(
                "RM %.2f",
                shipping
            )

        txtTotal.text =
            String.format(
                "RM %.2f",
                total
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