package com.sanaker.hvaskalvispise.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.Dish

class DishAdapter(
    private val onDeleteClicked: (Dish, Int) -> Unit,    // For sletteknappen
    private val onEditButtonClicked: (Dish) -> Unit,    // For den NYE redigeringsknappen
    private val onItemClicked: ((Dish) -> Unit)? = null // For klikk på hele raden (valgfri, derfor nullable og default null)
) : ListAdapter<Dish, DishAdapter.DishViewHolder>(DishDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dish, parent, false)
        // Send de riktige callbacks til ViewHolder-konstruktøren
        return DishViewHolder(view, onDeleteClicked, onEditButtonClicked, onItemClicked)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ViewHolder tar nå callbacks i konstruktøren
    inner class DishViewHolder(
        itemView: View,
        // Disse mottas nå fra onCreateViewHolder
        private val currentDeleteCallback: (Dish, Int) -> Unit,
        private val currentEditCallback: (Dish) -> Unit,
        private val currentItemClickCallback: ((Dish) -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {

        // Sørg for at disse ID-ene stemmer med din item_dish.xml
        private val textViewName: TextView = itemView.findViewById(R.id.dishNameTextView)
        private val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEditDish)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDeleteDish) // KORRIGERT ID (én 't')

        fun bind(dish: Dish) {
            textViewName.text = dish.name

            buttonEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    currentEditCallback(getItem(position)) // Bruk mottatt callback
                }
            }

            buttonDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    currentDeleteCallback(getItem(position), position) // Bruk mottatt callback
                }
            }

            if (currentItemClickCallback != null) {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        currentItemClickCallback.invoke(getItem(position)) // Bruk mottatt callback
                    }
                }
            } else {
                itemView.setOnClickListener(null)
            }
        }
    }

    class DishDiffCallback : DiffUtil.ItemCallback<Dish>() {
        override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem == newItem
        }
    }
}
