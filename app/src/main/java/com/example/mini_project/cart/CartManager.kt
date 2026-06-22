package com.example.mini_project.cart

import com.example.mini_project.model.Album
import com.example.mini_project.model.Cart

object CartManager {

    private val items = mutableListOf<Cart>()

    fun addToCart(album: Album) {
        val existing = items.find { it.album.id == album.id }

        if (existing != null) {
            existing.quantity++
        } else {
            items.add(Cart(album, 1))
        }
    }

    fun removeFromCart(album: Album) {
        items.removeAll { it.album.id == album.id }
    }

    fun getCartItems(): List<Cart> = items

    fun getTotal(): Double {
        return items.sumOf { it.album.price * it.quantity }
    }

    fun clear() {
        items.clear()
    }
}