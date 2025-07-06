package com.sanaker.hvaskalvispise.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.Dish
import com.sanaker.hvaskalvispise.ui.adapter.DishAdapter
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModel

class DishListActivity : AppCompatActivity {

    private lateinit var viewModel: MainViewModel
    private lateinit var dishAdapter: DishAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish_list)

        // Initialize ViewModel
        viewModel = viewModelProvider(this).get(MainViewModel::class.java)

        // Set uo RecyclerView
        val recyclerViewDishes = findViewById<RecyclerView>(R.id.recylerViewDishes)
        recyclerViewDishes.layoutManager = LinearLayoutManager(this)
        dishAdapter = DishAdapter()
        recyclerViewDishes.adapter = dishAdapter

        // Observe the list of dishes and update the adapter
        viewModel.dishes.observe(this, { dishes ->
            dishAdapter.dishes = dishes
            dishAdapter.notifyDataSetChanged()
        })
    }
}