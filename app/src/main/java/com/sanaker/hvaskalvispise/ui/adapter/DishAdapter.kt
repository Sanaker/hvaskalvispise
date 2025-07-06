package com.sanaker.hvaskalvispise.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.Dish

class DishAdapter : RecyclerView.Adapter<DishAdapter.DishViewHolder>() {

    var dishes: List<Dish> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dish, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishes[position]
        holder.textViewName.text = dish.name
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewDishName)
    }
}
