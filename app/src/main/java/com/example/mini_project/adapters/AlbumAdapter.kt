package com.example.mini_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.R
import com.example.mini_project.cart.CartManager
import com.example.mini_project.model.Album

class AlbumAdapter(
    private var albumList: List<Album>
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    fun updateList(newList: List<Album>) {
        albumList = newList
        notifyDataSetChanged()
    }

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageAlbum)
        val title: TextView = itemView.findViewById(R.id.tvAlbumTitle)
        val artist: TextView = itemView.findViewById(R.id.tvArtist)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val btnAdd: AppCompatButton = itemView.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumList[position]

        holder.image.setImageResource(album.imageRes)
        holder.title.text = album.title
        holder.artist.text = album.artist
        holder.price.text = String.format("RM %.2f", album.price)

        holder.btnAdd.setOnClickListener {
            CartManager.addToCart(album)

            Toast.makeText(
                holder.itemView.context,
                "Added to cart",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = albumList.size
}