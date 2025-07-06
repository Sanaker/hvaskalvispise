package com.sanaker.hvaskalvispise.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider // Import ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.AppDatabase // Import your database class
import com.sanaker.hvaskalvispise.data.model.Dish // Import your Dish entity
import com.sanaker.hvaskalvispise.data.model.DishDao // Import your DAO
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModel // Import MainViewModel
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModelFactory // We will create this factory
import kotlinx.coroutines.Dispatchers // For specifying coroutine dispatcher (e.g., IO thread)
import kotlinx.coroutines.launch // To launch coroutines
import kotlinx.coroutines.withContext // To switch context back to Main thread
// Removed unnecessary Random import, as ViewModel handles random choice

class MainActivity : AppCompatActivity() {

    private lateinit var selectedDishTextView: TextView
    private lateinit var chooseDishButton: Button
    private lateinit var newDishEditText: EditText
    private lateinit var addDishButton: Button
    private lateinit var dishesRecyclerView: RecyclerView
    private lateinit var dishesAdapter: DishesAdapter
    private lateinit var mainViewModel: MainViewModel // Use the ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views from your layout
        selectedDishTextView = findViewById(R.id.selectedDishTextView)
        chooseDishButton = findViewById(R.id.chooseDishButton)
        newDishEditText = findViewById(R.id.newDishEditText)
        addDishButton = findViewById(R.id.addDishButton)
        dishesRecyclerView = findViewById(R.id.dishesRecyclerView)

        // Initialize Room Database DAO
        val dishDao = AppDatabase.getDatabase(applicationContext).dishDao()

        // Initialize ViewModel using a Factory to pass the DishDao
        val viewModelFactory = MainViewModelFactory(dishDao)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        // Set up RecyclerView
        dishesAdapter = DishesAdapter(emptyList()) // This should now work as DishesAdapter imports Dish correctly
        dishesRecyclerView.layoutManager = LinearLayoutManager(this)
        dishesRecyclerView.adapter = dishesAdapter

        // Observe dishes from the ViewModel (which gets them from the database)
        mainViewModel.dishes.observe(this) { dishes ->
            dishesAdapter.updateDishes(dishes)

            // Optional: Update selectedDishTextView initially if there are dishes
            if (selectedDishTextView.text == "No dish selected" && dishes.isNotEmpty()) {
                selectedDishTextView.text = dishes[0].name // Display the first dish by default
            } else if (dishes.isEmpty()) {
                selectedDishTextView.text = "No dish selected"
            }
        }

        // Observe selectedDish from the ViewModel
        mainViewModel.selectedDish.observe(this) { selectedDish ->
            selectedDishTextView.text = selectedDish?.name ?: "Add some dishes first!"
        }

        // Set up button listeners

        // Add Dish Button
        addDishButton.setOnClickListener {
            val dishName = newDishEditText.text.toString().trim()
            if (dishName.isNotEmpty()) {
                mainViewModel.addDish(dishName) // Use ViewModel to add dish
                newDishEditText.text.clear() // Clear EditText directly here (UI operation)
            }
        }

        // Choose Random Dish Button
        chooseDishButton.setOnClickListener {
            mainViewModel.chooseRandomDish() // Use ViewModel to choose random dish
        }
    }
}