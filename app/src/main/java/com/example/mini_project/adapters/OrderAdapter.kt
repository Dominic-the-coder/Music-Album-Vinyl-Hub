package com.example.mini_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mini_project.R
import com.example.mini_project.backend.AlbumDTO
import com.example.mini_project.backend.OrderDTO
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val orders: MutableList<OrderDTO>,
    private var albums: List<AlbumDTO> = emptyList(),
    private val onViewOrder: ((OrderDTO) -> Unit)? = null
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtOrderNumber: TextView =
            itemView.findViewById(R.id.txtOrderNumber)

        val txtOrderStatus: TextView =
            itemView.findViewById(R.id.txtOrderStatus)

        val txtOrderDate: TextView =
            itemView.findViewById(R.id.txtOrderDate)

        val imgOrderAlbum: ImageView =
            itemView.findViewById(R.id.imgOrderAlbum)

        val txtOrderAlbumTitle: TextView =
            itemView.findViewById(R.id.txtOrderAlbumTitle)

        val txtOrderArtist: TextView =
            itemView.findViewById(R.id.txtOrderArtist)

        val txtOrderQuantity: TextView =
            itemView.findViewById(R.id.txtOrderQuantity)

        val txtOrderTotal: TextView =
            itemView.findViewById(R.id.txtOrderTotal)

        val btnViewOrder: View =
            itemView.findViewById(R.id.btnViewOrder)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_order,
                    parent,
                    false
                )

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {

        val order = orders[position]

        holder.txtOrderNumber.text =
            "Order #${order.id}"

        holder.txtOrderStatus.text =
            order.status

        holder.txtOrderDate.text =
            formatDate(order.createdAt)

        holder.txtOrderTotal.text =
            String.format(
                Locale.US,
                "RM %.2f",
                order.totalAmount
            )

        val orderItem =
            order.items.firstOrNull()

        if (orderItem != null) {

            holder.txtOrderAlbumTitle.text =
                orderItem.albumTitle

            holder.txtOrderQuantity.text =
                "Qty: ${orderItem.quantity}"

            val album =
                albums.find {
                    it.id == orderItem.albumId
                }

            if (album != null) {

                holder.txtOrderArtist.text =
                    album.artist

                Glide.with(holder.itemView.context)
                    .load(album.imageUrl)
                    .placeholder(R.drawable.vinyl_pop)
                    .error(R.drawable.vinyl_pop)
                    .into(holder.imgOrderAlbum)

            } else {

                holder.txtOrderArtist.text =
                    "Unknown Artist"

                holder.imgOrderAlbum.setImageResource(
                    R.drawable.vinyl_pop
                )
            }

        } else {

            holder.txtOrderAlbumTitle.text =
                "No items"

            holder.txtOrderArtist.text =
                ""

            holder.txtOrderQuantity.text =
                ""

            holder.imgOrderAlbum.setImageResource(
                R.drawable.vinyl_pop
            )
        }

        holder.btnViewOrder.setOnClickListener {
            onViewOrder?.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun updateList(
        newOrders: List<OrderDTO>
    ) {

        orders.clear()
        orders.addAll(newOrders)

        notifyDataSetChanged()
    }

    fun updateAlbums(
        newAlbums: List<AlbumDTO>
    ) {

        albums = newAlbums

        notifyDataSetChanged()
    }

    private fun formatDate(
        dateString: String
    ): String {

        return try {

            val inputFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
                )

            val outputFormat =
                SimpleDateFormat(
                    "d MMMM yyyy",
                    Locale.getDefault()
                )

            val date =
                inputFormat.parse(dateString)

            if (date != null) {
                outputFormat.format(date)
            } else {
                dateString
            }

        } catch (e: Exception) {

            dateString
        }
    }
}