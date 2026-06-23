package com.example.mini_project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.adapters.CartAdapter
import com.example.mini_project.cart.CartManager
import android.view.View
import android.widget.LinearLayout

class CartActivity : AppCompatActivity() {

    private lateinit var adapter: CartAdapter
    private lateinit var tvTotal: TextView
    private lateinit var btnConfirm: AppCompatButton
    private lateinit var emptyLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnConfirm = findViewById(R.id.btnConfirm)
        emptyLayout = findViewById(R.id.layoutEmptyCart)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        // Initialize CartAdapter with current cart items and handle item removal
        adapter = CartAdapter(
            CartManager.getCartItems().toMutableList()
        ) { item ->
            // Remove selected album from cart
            CartManager.removeFromCart(item.album)
            // Refresh UI after data change
            refreshCart()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnConfirm.setOnClickListener {

            // Prevent checkout when cart is empty
            if (CartManager.getCartItems().isEmpty()) return@setOnClickListener

            // Complete purchase by clearing cart
            CartManager.clear()

            // Navigate back to album list screen
            startActivity(
                Intent(this, AlbumListActivity::class.java)
            )

            // Close current activity
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }

        refreshCart()
    }

    private fun refreshCart() {

        // Get latest cart data
        val items = CartManager.getCartItems().toMutableList()

        // Update RecyclerView with current cart items
        adapter.updateList(items)

        // Calculate and display total cart price
        tvTotal.text = String.format(
            "Total: RM %.2f",
            CartManager.getTotal()
        )

        if (items.isEmpty()) {
            // Show empty cart message and hide checkout section
            emptyLayout.visibility = View.VISIBLE
            tvTotal.visibility = View.GONE
            btnConfirm.visibility = View.GONE
        } else {
            // Show cart summary and checkout button
            emptyLayout.visibility = View.GONE
            tvTotal.visibility = View.VISIBLE
            btnConfirm.visibility = View.VISIBLE
        }
    }
}