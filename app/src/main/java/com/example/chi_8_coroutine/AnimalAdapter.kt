package com.example.chi_8_coroutine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class AnimalAdapter() : RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {
    private var items: List<Animal> = mutableListOf()

    inner class AnimalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(animal: Animal) {
            itemView.run {
                val name = findViewById<TextView>(R.id.name)
                name.text = animal.name

                val latinName = findViewById<TextView>(R.id.latin_name)
                latinName.text = animal.latin_name

                val animalPhoto = findViewById<ImageView>(R.id.icon)
                Glide.with(this)
                    .load(animal.image_link)
                    .transition(DrawableTransitionOptions.withCrossFade(3000))
                    .into(animalPhoto)

                val description =
                    "This animal is a representative of the ${animal.animal_type} class.  " +
                            "The peak of activity of this creature came in the ${animal.active_time}" +
                            " The dimensions of one person are from ${animal.length_min} to ${animal.length_max} meters, " +
                            "and the weight is from ${animal.weight_min} to ${animal.weight_max} kilograms." +
                            "For habitat prefers ${animal.habitat}, mostly in ${animal.geo_range}. " +
                            "These places are rich in ${animal.diet}, which is the animal's main diet." +
                            " Average life expectancy - ${animal.lifespan} years "

                val descriptionView = findViewById<TextView>(R.id.description)
                descriptionView.text = description
            }
        }
    }

    fun setItems(list :List<Animal>){
        this.items = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fragment, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = items[position]
        holder.bind(animal)
    }

    override fun getItemCount(): Int = items.size
}