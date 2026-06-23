package com.example.mini_project.cart

import com.example.mini_project.model.Album
import com.example.mini_project.model.Cart

object CartManager {

    private val items = mutableListOf<Cart>()

    // Adds an album to the cart or increases its quantity if it already exists
    fun addToCart(album: Album) {
        val existing = items.find { it.album.id == album.id }

        if (existing != null) {
            existing.quantity++
        } else {
            items.add(Cart(album, 1))
        }
    }

    // Remove the selected album from the cart
    fun removeFromCart(album: Album) {
        items.removeAll { it.album.id == album.id }
    }

    // Return all items currently in the cart
    fun getCartItems(): List<Cart> = items

    // Calculate the total price of all cart items
    fun getTotal(): Double {
        return items.sumOf { it.album.price * it.quantity }
    }

    // Clear all cart items after the order is confirmed
    fun clear() {
        items.clear()
    }
}