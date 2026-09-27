package com.example.mini_project.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mini_project.R
import com.example.mini_project.adapters.SongAdapter
import com.example.mini_project.backend.AlbumDTO
import com.example.mini_project.backend.ApiService
import com.example.mini_project.backend.Auth
import com.example.mini_project.backend.CartDTO
import com.example.mini_project.backend.CartService
import com.example.mini_project.instance.RetrofitInstance
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumDetailFragment :
    Fragment(R.layout.fragment_album_detail) {

    private lateinit var apiService: ApiService
    private lateinit var cartService: CartService

    private lateinit var imgAlbumDetail: ImageView
    private lateinit var txtAlbumDetailTitle: TextView
    private lateinit var txtAlbumDetailArtist: TextView
    private lateinit var txtAlbumDetailPrice: TextView
    private lateinit var btnDetailAddCart: MaterialButton
    private lateinit var songRecyclerView: RecyclerView

    private var songAdapter: SongAdapter? = null


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        apiService =
            RetrofitInstance.getApi(
                requireContext()
            )

        cartService =
            CartService(apiService)

        imgAlbumDetail =
            view.findViewById(
                R.id.imgAlbumDetail
            )

        txtAlbumDetailTitle =
            view.findViewById(
                R.id.txtAlbumDetailTitle
            )

        txtAlbumDetailArtist =
            view.findViewById(
                R.id.txtAlbumDetailArtist
            )

        txtAlbumDetailPrice =
            view.findViewById(
                R.id.txtAlbumDetailPrice
            )

        btnDetailAddCart =
            view.findViewById(
                R.id.btnDetailAddCart
            )

        songRecyclerView =
            view.findViewById(
                R.id.songRecyclerView
            )

        val btnBack =
            view.findViewById<ImageButton>(
                R.id.btnBack
            )


        songRecyclerView.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        songRecyclerView.isNestedScrollingEnabled =
            false


        // ========================================================
        // BACK BUTTON
        // ========================================================

        btnBack.setOnClickListener {

            songAdapter?.stopMusic()

            parentFragmentManager
                .popBackStack()
        }


        val albumId =
            arguments?.getInt(
                "albumId",
                -1
            ) ?: -1


        if (albumId == -1) {

            showToast(
                "Album could not be found."
            )

            parentFragmentManager
                .popBackStack()

            return
        }


        loadAlbum(albumId)
    }


    // ============================================================
    // LOAD ALBUM
    // ============================================================

    private fun loadAlbum(
        albumId: Int
    ) {

        apiService.getAlbums().enqueue(
            object : Callback<List<AlbumDTO>> {

                override fun onResponse(
                    call: Call<List<AlbumDTO>>,
                    response: Response<List<AlbumDTO>>
                ) {

                    if (!isAdded) {
                        return
                    }


                    if (!response.isSuccessful) {

                        showToast(
                            "Unable to load album information."
                        )

                        return
                    }


                    val albums =
                        response.body()
                            ?: emptyList()


                    val album =
                        albums.find {
                            it.id == albumId
                        }


                    if (album == null) {

                        showToast(
                            "Album could not be found."
                        )

                        parentFragmentManager
                            .popBackStack()

                        return
                    }


                    displayAlbum(album)
                }


                override fun onFailure(
                    call: Call<List<AlbumDTO>>,
                    t: Throwable
                ) {

                    if (!isAdded) {
                        return
                    }


                    showToast(
                        "Unable to load album. Please try again."
                    )
                }
            }
        )
    }


    // ============================================================
    // DISPLAY ALBUM
    // ============================================================

    private fun displayAlbum(
        album: AlbumDTO
    ) {

        txtAlbumDetailTitle.text =
            album.title

        txtAlbumDetailArtist.text =
            album.artist

        txtAlbumDetailPrice.text =
            String.format(
                "RM %.2f",
                album.price
            )


        // ========================================================
        // ALBUM IMAGE
        // ========================================================

        if (
            !album.imageUrl.isNullOrEmpty()
        ) {

            Glide.with(this)
                .load(album.imageUrl)
                .placeholder(
                    R.drawable.vinyl_banner
                )
                .error(
                    R.drawable.vinyl_banner
                )
                .into(imgAlbumDetail)

        } else {

            imgAlbumDetail.setImageResource(
                R.drawable.vinyl_banner
            )
        }


        // ========================================================
        // SONGS
        // ========================================================

        songAdapter =
            SongAdapter(album.songs)

        songRecyclerView.adapter =
            songAdapter


        // ========================================================
        // ADD TO CART
        // ========================================================

        btnDetailAddCart.setOnClickListener {

            addToCart(album)
        }
    }


    // ============================================================
    // ADD TO CART
    // ============================================================

    private fun addToCart(
        album: AlbumDTO
    ) {

        val context =
            requireContext()


        val userId =
            Auth.getUserId(context)


        if (userId == -1) {

            showToast(
                "Please log in to add items to your cart."
            )

            return
        }


        btnDetailAddCart.isEnabled =
            false


        cartService
            .addToCart(
                userId = userId,
                albumId = album.id,
                quantity = 1
            )
            .enqueue(
                object : Callback<CartDTO> {

                    override fun onResponse(
                        call: Call<CartDTO>,
                        response: Response<CartDTO>
                    ) {

                        if (!isAdded) {
                            return
                        }


                        btnDetailAddCart.isEnabled =
                            true


                        if (response.isSuccessful) {

                            showToast(
                                "${album.title} added to your cart."
                            )

                        } else {

                            showToast(
                                "Unable to add this album to your cart."
                            )
                        }
                    }


                    override fun onFailure(
                        call: Call<CartDTO>,
                        t: Throwable
                    ) {

                        if (!isAdded) {
                            return
                        }


                        btnDetailAddCart.isEnabled =
                            true


                        showToast(
                            "Unable to add the album to your cart. Please try again."
                        )
                    }
                }
            )
    }


    // ============================================================
    // TOAST HELPER
    // ============================================================

    private fun showToast(
        message: String
    ) {

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }


    // ============================================================
    // STOP MUSIC
    // ============================================================

    override fun onDestroyView() {

        songAdapter?.stopMusic()

        songAdapter = null

        super.onDestroyView()
    }
}