package com.example.mini_project.services

import com.example.mini_project.adapters.AlbumAdapter
import com.example.mini_project.model.Album

open class AlbumService (

    // Stores all album data
    private var albumList: List<Album>,

    // RecyclerView adapter reference
    private var currAdapter: AlbumAdapter
){

    // Currently displayed albums
    var currentSelectedAlbums: MutableList<Album> = albumList.toMutableList()

    // Adapter used to refresh RecyclerView
    var adapter: AlbumAdapter = currAdapter

    // Updates displayed albums and refreshes RecyclerView
    fun updateList(filtered: List<Album>){
        currentSelectedAlbums = filtered.toMutableList()
        adapter.updateList(currentSelectedAlbums)
    }

    // Returns the current displayed album list
    fun retrieveCurrentSelectedAlbums(): MutableList<Album>{
        return currentSelectedAlbums
    }
}