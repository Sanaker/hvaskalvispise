package com.sanaker.hvaskalvispise.ui.dishlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.Dish

// DishAdapter tar en lambda-funksjon (onDeleteClicked) som parameter
// Denne lambda-funksjonen blir kalt når sletteknappen trykkes på et element
class DishAdapter(private val onDeleteClicked: (Dish) -> Unit) :
    RecyclerView.Adapter<DishAdapter.DishViewHolder>() {

    private val dishes = mutableListOf<Dish>() // Listen av retter adapteren skal vise

    // Oppretter en ViewHolder når RecyclerView trenger en ny en (f.eks. ved scrolling)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dish, parent, false)
        return DishViewHolder(view)
    }

    // Binder data til en ViewHolder
    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishes[position]
        holder.bind(dish, onDeleteClicked)
    }

    // Returnerer antall elementer i listen
    override fun getItemCount(): Int = dishes.size

    // Oppdaterer listen av retter og varsler adapteren om endringen
    fun submitList(newList: List<Dish>) {
        dishes.clear()
        dishes.addAll(newList)
        notifyDataSetChanged() // Forteller RecyclerView at dataene har endret seg
    }

    // ViewHolder-klasse som holder referanser til UI-elementene i item_dish.xml
    class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dishNameTextView: TextView = itemView.findViewById(R.id.dishNameTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(dish: Dish, onDeleteClicked: (Dish) -> Unit) {
            dishNameTextView.text = dish.name
            deleteButton.setOnClickListener {
                onDeleteClicked(dish) // Kall lambda-funksjonen når sletteknappen trykkes
            }
        }
    }
}