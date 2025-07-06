package com.sanaker.hvaskalvispise.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.Dish // Import your Dish entity

class DishesAdapter(private var dishes: List<Dish>) :
    RecyclerView.Adapter<DishesAdapter.DishViewHolder>() {

    // ViewHolder holds the view for each item in the list
    class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishNameTextView: TextView = itemView.findViewById(R.id.dishNameTextView) // Assuming you'll add this ID to list_item_dish.xml
    }

    // Creates new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        // Inflate the layout for a single list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_dish, parent, false) // We will create list_item_dish.xml next
        return DishViewHolder(view)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val currentDish = dishes[position]
        holder.dishNameTextView.text = currentDish.name
        // You can add more click listeners or other logic here if needed
    }

    // Returns the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dishes.size
    }

    // Method to update the list of dishes and notify the adapter of changes
    fun updateDishes(newDishes: List<Dish>) {
        dishes = newDishes
        notifyDataSetChanged() // Notifies the RecyclerView to refresh its items
    }
}