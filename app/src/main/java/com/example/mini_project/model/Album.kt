package com.example.mini_project.models

data class Album(
    val id: Int,
    val jamendoId: String,
    val title: String,
    val artist: String,
    val imageUrl: String?,
    val releaseDate: String?,
    val price: Double,
    val quantity: Int,
    val songs: List<Song>
)