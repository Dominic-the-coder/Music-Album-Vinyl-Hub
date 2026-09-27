package com.example.mini_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mini_project.R
import com.example.mini_project.backend.AlbumDTO
import com.example.mini_project.backend.CartItemDTO

class CartAdapter(
    private var cartList: MutableList<CartItemDTO>,
    private var albums: List<AlbumDTO>,
    private val onDelete: (CartItemDTO) -> Unit,
    private val onChange: (CartItemDTO) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgAlbum: ImageView =
            itemView.findViewById(R.id.imgAlbum)

        val txtAlbumTitle: TextView =
            itemView.findViewById(R.id.txtAlbumTitle)

        val txtAlbumArtist: TextView =
            itemView.findViewById(R.id.txtAlbumArtist)

        val txtAlbumPrice: TextView =
            itemView.findViewById(R.id.txtAlbumPrice)

        val txtQuantity: TextView =
            itemView.findViewById(R.id.txtQuantity)

        val btnPlus: ImageButton =
            itemView.findViewById(R.id.btnPlus)

        val btnMinus: ImageButton =
            itemView.findViewById(R.id.btnMinus)

        val btnDelete: ImageView =
            itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_cart,
                parent,
                false
            )

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {

        val item = cartList[position]

        val album = albums.find {
            it.id == item.albumId
        }

        // Album image
        Glide.with(holder.itemView.context)
            .load(album?.imageUrl)
            .placeholder(R.drawable.vinyl_pop)
            .error(R.drawable.vinyl_pop)
            .into(holder.imgAlbum)

        // Album title
        holder.txtAlbumTitle.text =
            album?.title ?: "Album #${item.albumId}"

        // Album artist
        holder.txtAlbumArtist.text =
            album?.artist ?: "Unknown Artist"

        // Album price
        holder.txtAlbumPrice.text =
            String.format(
                "RM %.2f",
                item.price
            )

        // Quantity
        holder.txtQuantity.text =
            item.quantity.toString()

        // Plus button
        holder.btnPlus.setOnClickListener {

            val currentPosition =
                holder.bindingAdapterPosition

            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            val currentItem =
                cartList[currentPosition]

            val updatedItem =
                currentItem.copy(
                    quantity = currentItem.quantity + 1
                )

            cartList[currentPosition] = updatedItem

            holder.txtQuantity.text =
                updatedItem.quantity.toString()

            onChange(updatedItem)
        }

        // Minus button
        holder.btnMinus.setOnClickListener {

            val currentPosition =
                holder.bindingAdapterPosition

            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            val currentItem =
                cartList[currentPosition]

            if (currentItem.quantity <= 1) {
                return@setOnClickListener
            }

            val updatedItem =
                currentItem.copy(
                    quantity = currentItem.quantity - 1
                )

            cartList[currentPosition] = updatedItem

            holder.txtQuantity.text =
                updatedItem.quantity.toString()

            onChange(updatedItem)
        }

        // Delete button
        holder.btnDelete.setOnClickListener {

            val currentPosition =
                holder.bindingAdapterPosition

            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            onDelete(
                cartList[currentPosition]
            )
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    fun updateList(
        newList: List<CartItemDTO>
    ) {
        // Make a separate copy so the Activity list
        // and Adapter list don't affect each other.
        cartList = newList.toMutableList()

        notifyDataSetChanged()
    }

    fun updateAlbums(
        newAlbums: List<AlbumDTO>
    ) {
        albums = newAlbums

        notifyDataSetChanged()
    }
}