package com.example.mini_project.models

data class Song(
    val id: Int,
    val jamendoId: String,
    val title: String,
    val artist: String,
    val audioUrl: String?,
    val imageUrl: String?,
    val duration: Int
)