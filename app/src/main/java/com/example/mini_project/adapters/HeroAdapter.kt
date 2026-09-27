package com.example.mini_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.R
import com.example.mini_project.models.HeroItem
import com.google.android.material.button.MaterialButton

class HeroAdapter(
    private val heroItems: List<HeroItem>,
    private val onButtonClick: (HeroItem) -> Unit
) : RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    class HeroViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val image: ImageView =
            view.findViewById(R.id.imgHero)

        val title: TextView =
            view.findViewById(R.id.txtHeroTitle)

        val description: TextView =
            view.findViewById(R.id.txtHeroDescription)

        val button: MaterialButton =
            view.findViewById(R.id.btnHero)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeroViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_hero,
                parent,
                false
            )

        return HeroViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HeroViewHolder,
        position: Int
    ) {

        val hero = heroItems[position]

        holder.image.setImageResource(hero.image)

        holder.title.text = hero.title

        holder.description.text = hero.description

        holder.button.text = hero.buttonText

        holder.button.setOnClickListener {
            onButtonClick(hero)
        }
    }

    override fun getItemCount(): Int {
        return heroItems.size
    }
}