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


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
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
            findViewById(
                R.id.txtSubtotal
            )


        txtShipping =
            findViewById(
                R.id.txtShipping
            )


        txtTotal =
            findViewById(
                R.id.txtTotal
            )


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

                    // Update local quantity
                    val index =
                        cartItems.indexOfFirst {
                            it.id == updatedItem.id
                        }

                    if (index != -1) {

                        cartItems[index] =
                            updatedItem
                    }

                    // Update total immediately
                    calculateTotal()

                    // IMPORTANT:
                    // Save quantity to backend
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

                Toast.makeText(
                    this,
                    "Your cart is empty",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            if (cartId == -1) {

                Toast.makeText(
                    this,
                    "Cart not loaded",
                    Toast.LENGTH_SHORT
                ).show()

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
                object :
                    Callback<List<AlbumDTO>> {

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
                        }
                    }


                    override fun onFailure(
                        call: Call<List<AlbumDTO>>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@CartActivity,
                            "Album network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
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

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

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

                            Toast.makeText(
                                this@CartActivity,
                                "Items from server: ${cart?.items?.size ?: 0}",
                                Toast.LENGTH_LONG
                            ).show()


                            cartItems.clear()


                            if (cart != null) {

                                cartId =
                                    cart.id

                                cartItems.addAll(
                                    cart.items
                                )
                            }


                            adapter.updateList(
                                cartItems
                            )


                            calculateTotal()
                        }

                        else if (
                            response.code() == 404
                        ) {

                            cartId = -1

                            cartItems.clear()


                            adapter.updateList(
                                cartItems
                            )


                            calculateTotal()
                        }

                        else {

                            Toast.makeText(
                                this@CartActivity,
                                "Failed to load cart: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<CartDTO>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@CartActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
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
                                        it.id ==
                                                updatedItem.id
                                    }


                                if (index != -1) {

                                    cartItems[index] =
                                        updatedItem
                                }
                            }


                            calculateTotal()
                        }

                        else {

                            Toast.makeText(
                                this@CartActivity,
                                "Failed to save quantity",
                                Toast.LENGTH_SHORT
                            ).show()


                            // Reload database version
                            loadCart()
                        }
                    }


                    override fun onFailure(
                        call: Call<CartItemDTO>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@CartActivity,
                            "Failed to save quantity",
                            Toast.LENGTH_SHORT
                        ).show()


                        // Reload database version
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
                object :
                    Callback<PaymentResponse> {

                    override fun onResponse(
                        call: Call<PaymentResponse>,
                        response: Response<PaymentResponse>
                    ) {

                        if (!response.isSuccessful) {

                            val error =
                                response.errorBody()
                                    ?.string()
                                    ?: "Unknown server error"


                            Toast.makeText(
                                this@CartActivity,
                                "Checkout failed: $error",
                                Toast.LENGTH_LONG
                            ).show()

                            return
                        }


                        val paymentResponse =
                            response.body()


                        if (paymentResponse == null) {

                            Toast.makeText(
                                this@CartActivity,
                                "Payment response is empty",
                                Toast.LENGTH_LONG
                            ).show()

                            return
                        }


                        val checkoutUrl =
                            paymentResponse.checkoutUrl


                        if (checkoutUrl.isBlank()) {

                            Toast.makeText(
                                this@CartActivity,
                                "Stripe URL is empty",
                                Toast.LENGTH_LONG
                            ).show()

                            return
                        }


                        Toast.makeText(
                            this@CartActivity,
                            "Opening Stripe...",
                            Toast.LENGTH_SHORT
                        ).show()


                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(checkoutUrl)
                            )


                        startActivity(intent)
                    }


                    override fun onFailure(
                        call: Call<PaymentResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@CartActivity,
                            "Connection failed: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
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

                            cartItems.remove(
                                item
                            )


                            adapter.updateList(
                                cartItems
                            )


                            calculateTotal()


                            Toast.makeText(
                                this@CartActivity,
                                "Removed from cart",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        else {

                            Toast.makeText(
                                this@CartActivity,
                                "Failed to remove item",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<Void>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@CartActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
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
}