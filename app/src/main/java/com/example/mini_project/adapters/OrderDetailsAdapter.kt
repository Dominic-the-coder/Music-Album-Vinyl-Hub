package com.example.mini_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.R
import com.example.mini_project.backend.OrderItemDTO
import java.util.Locale

class OrderDetailsAdapter(
    private val items:
    MutableList<OrderItemDTO>
) : RecyclerView.Adapter<OrderDetailsAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtAlbumTitle: TextView =
            itemView.findViewById(
                R.id.txtAlbumTitle
            )

        val txtQuantity: TextView =
            itemView.findViewById(
                R.id.txtQuantity
            )

        val txtPrice: TextView =
            itemView.findViewById(
                R.id.txtPrice
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderItemViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_order_detail,
                parent,
                false
            )

        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: OrderItemViewHolder,
        position: Int
    ) {

        val item =
            items[position]

        holder.txtAlbumTitle.text =
            item.albumTitle

        holder.txtQuantity.text =
            "Qty: ${item.quantity}"

        val itemTotal =
            item.price * item.quantity

        holder.txtPrice.text =
            String.format(
                Locale.US,
                "RM %.2f",
                itemTotal
            )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateList(
        newItems: List<OrderItemDTO>
    ) {

        items.clear()
        items.addAll(newItems)

        notifyDataSetChanged()
    }
}