package com.example.mini_project.services

import com.example.mini_project.adapters.AlbumAdapter
import com.example.mini_project.model.Album

open class AlbumService (
    private var albumList: List<Album>,
    private var currAdapter: AlbumAdapter
){

    var currentSelectedAlbums: MutableList<Album>  = albumList.toMutableList()
    var adapter: AlbumAdapter = currAdapter


    fun updateList(filtered: List<Album>){
        currentSelectedAlbums = filtered.toMutableList()
        adapter.updateList(currentSelectedAlbums)
    }

    fun retrieveCurrentSelectedAlbums():MutableList<Album>{
        return currentSelectedAlbums
    }
}