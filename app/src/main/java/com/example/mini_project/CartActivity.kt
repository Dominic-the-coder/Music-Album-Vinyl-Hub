package com.example.mini_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.adapters.CartAdapter
import com.example.mini_project.cart.CartManager

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnConfirm = findViewById(R.id.btnConfirm)
        emptyLayout = findViewById(R.id.layoutEmptyCart)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        adapter = CartAdapter(
            CartManager.getCartItems().toMutableList(),
            onDelete = { item ->
                CartManager.removeFromCart(item.album)
                refreshCart()
            },
            onChange = {
                refreshCart()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnConfirm.setOnClickListener {
            CartManager.clear()
            startActivity(Intent(this, AlbumListActivity::class.java))
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }

        refreshCart()
    }

    private fun refreshCart() {

        val items = CartManager.getCartItems().toMutableList()

        adapter.updateList(items)

        tvTotal.text = String.format(
            "Total: RM %.2f",
            CartManager.getTotal()
        )

        if (items.isEmpty()) {
            emptyLayout.visibility = View.VISIBLE
            tvTotal.visibility = View.GONE
            btnConfirm.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.GONE
            tvTotal.visibility = View.VISIBLE
            btnConfirm.visibility = View.VISIBLE
        }
    }
}