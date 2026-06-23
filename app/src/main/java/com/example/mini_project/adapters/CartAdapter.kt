package com.example.mini_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.R
import com.example.mini_project.model.Cart

class CartAdapter(
    private var cartList: MutableList<Cart>,
    private val onDelete: (Cart) -> Unit,
    private val onChange: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.imageAlbum)
        val title: TextView = itemView.findViewById(R.id.tvAlbumTitle)
        val artist: TextView = itemView.findViewById(R.id.tvArtist)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val qty: TextView = itemView.findViewById(R.id.tvQty)

        val btnPlus: ImageView = itemView.findViewById(R.id.btnPlus)
        val btnMinus: ImageView = itemView.findViewById(R.id.btnMinus)
        val delete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val item = cartList[position]

        holder.image.setImageResource(item.album.imageRes)
        holder.title.text = item.album.title
        holder.artist.text = item.album.artist

        val itemTotal = item.album.price * item.quantity
        holder.price.text = String.format("RM %.2f", itemTotal)

        holder.qty.text = "Qty: ${item.quantity}"

        holder.btnPlus.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION || pos >= cartList.size) return@setOnClickListener

            cartList[pos].quantity++
            notifyItemChanged(pos)
            onChange()
        }

        holder.btnMinus.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION || pos >= cartList.size) return@setOnClickListener

            val itemNow = cartList[pos]

            if (itemNow.quantity > 1) {
                itemNow.quantity--
                notifyItemChanged(pos)
            }
        }

        holder.delete.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION || pos >= cartList.size) return@setOnClickListener

            val itemNow = cartList[pos]

            onDelete(itemNow)
            onChange()

            Toast.makeText(
                holder.itemView.context,
                "Removed from cart",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = cartList.size

    fun updateList(newList: MutableList<Cart>) {
        cartList = newList
        notifyDataSetChanged()
    }
}