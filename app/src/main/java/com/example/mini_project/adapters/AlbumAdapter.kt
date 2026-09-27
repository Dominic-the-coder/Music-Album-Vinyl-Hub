package com.example.mini_project.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mini_project.R
import com.example.mini_project.backend.AlbumDTO
import com.google.android.material.button.MaterialButton

class AlbumAdapter(
    private var albums: List<AlbumDTO>,
    private val onAddToCartClick: (AlbumDTO) -> Unit,
    private val onAlbumClick: (AlbumDTO) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val albumImage: ImageView =
            itemView.findViewById(R.id.imgAlbum)

        val albumTitle: TextView =
            itemView.findViewById(R.id.txtAlbumTitle)

        val albumArtist: TextView =
            itemView.findViewById(R.id.txtArtist)

        val albumPrice: TextView =
            itemView.findViewById(R.id.txtPrice)

        val addToCartButton: MaterialButton =
            itemView.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)

        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AlbumViewHolder,
        position: Int
    ) {

        val album = albums[position]

        holder.albumTitle.text = album.title
        holder.albumArtist.text = album.artist
        holder.albumPrice.text =
            String.format("RM %.2f", album.price)

        if (!album.imageUrl.isNullOrEmpty()) {

            Glide.with(holder.itemView.context)
                .load(album.imageUrl)
                .placeholder(R.drawable.vinyl_banner)
                .error(R.drawable.vinyl_banner)
                .into(holder.albumImage)

        } else {

            holder.albumImage.setImageResource(
                R.drawable.vinyl_banner
            )
        }

        holder.itemView.setOnClickListener {

            Log.d(
                "AlbumAdapter",
                "Album clicked: ${album.title}"
            )

            onAlbumClick(album)
        }

        holder.addToCartButton.setOnClickListener {

            Log.d(
                "AlbumAdapter",
                "ADD TO CART CLICKED: ${album.title}, ID=${album.id}"
            )

            onAddToCartClick(album)
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    fun updateAlbums(newAlbums: List<AlbumDTO>) {

        albums = newAlbums

        notifyDataSetChanged()
    }
}