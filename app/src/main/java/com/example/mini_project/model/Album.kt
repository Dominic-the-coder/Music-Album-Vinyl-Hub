package com.example.mini_project.model

data class Album(
    val id: Int =0,
    val title: String,
    val artist: String,
    val price: Double,
    val imageRes: Int,
    var quantity: Int = 0
)