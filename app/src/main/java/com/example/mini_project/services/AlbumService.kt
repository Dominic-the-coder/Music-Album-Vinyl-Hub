package com.example.mini_project.services

import com.example.mini_project.adapters.AlbumAdapter
import com.example.mini_project.backend.AlbumDTO

open class AlbumService(

    private var albumList: List<AlbumDTO>,

    private var currAdapter: AlbumAdapter

) {

    var currentSelectedAlbums: MutableList<AlbumDTO> =
        albumList.toMutableList()

    var adapter: AlbumAdapter =
        currAdapter


    fun updateList(
        filtered: List<AlbumDTO>
    ) {

        currentSelectedAlbums =
            filtered.toMutableList()

        adapter.updateAlbums(
            currentSelectedAlbums
        )
    }


    fun retrieveCurrentSelectedAlbums():
            MutableList<AlbumDTO> {

        return currentSelectedAlbums
    }
}