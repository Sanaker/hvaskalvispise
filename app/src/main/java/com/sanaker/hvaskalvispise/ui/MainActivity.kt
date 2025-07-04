package com.sanaker.hvaskalvispise.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.Dish
import com.sanaker.hvaskalvispise.data.repository.DishRepository
import com.sanaker.hvaskalvispise.ui.dishlists.DishAdapter
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras // This is crucial for the modern ViewModel.Factory create method
import kotlin.reflect.KClass // Needed if using KClass in the ViewModel Factory

// For å instansiere ViewModel med repository (senere bruker vi Dagger Hilt/Koin for dette)
class MainViewModelFactory(private val repository: DishRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var dishAdapter: DishAdapter

    private lateinit var selectedDishTextView: TextView
    private lateinit var chooseDishButton: Button
    private lateinit var newDishEditText: EditText
    private lateinit var addDishButton: Button
    private lateinit var dishesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser UI-elementer
        selectedDishTextView = findViewById(R.id.selectedDishTextView)
        chooseDishButton = findViewById(R.id.chooseDishButton)
        newDishEditText = findViewById(R.id.newDishEditText)
        addDishButton = findViewById(R.id.addDishButton)
        dishesRecyclerView = findViewById(R.id.dishesRecyclerView)

        // Initialiser DishRepository og ViewModel
        val repository = DishRepository(this)
        viewModel = ViewModelProvider(this, MainViewModelFactory(repository)).get(MainViewModel::class.java)

        // Sett opp RecyclerView og adapter
        dishAdapter = DishAdapter { dishToDelete ->
            // Dette er lambda-funksjonen som kalles når sletteknappen i adapteren trykkes
            showDeleteConfirmationDialog(dishToDelete)
        }
        dishesRecyclerView.layoutManager = LinearLayoutManager(this)
        dishesRecyclerView.adapter = dishAdapter

        // Observerer LiveData fra ViewModel
        // Når listen over retter endrer seg i ViewModel, oppdateres UI automatisk
        viewModel.dishes.observe(this) { dishes ->
            dishAdapter.submitList(dishes)
        }

        // Observerer den valgte retten fra ViewModel
        viewModel.selectedDish.observe(this) { dish ->
            selectedDishTextView.text = dish?.name ?: getString(R.string.no_dish_selected)
        }

        // Sett opp knappeloggikk
        addDishButton.setOnClickListener {
            val dishName = newDishEditText.text.toString().trim()
            if (dishName.isNotEmpty()) {
                viewModel.addDish(dishName)
                newDishEditText.text.clear() // Tøm inputfeltet
                Toast.makeText(this, R.string.dish_added_message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Vennligst skriv inn en rett", Toast.LENGTH_SHORT).show()
            }
        }

        chooseDishButton.setOnClickListener {
            viewModel.chooseRandomDish()
        }
    }

    // Viser en bekreftelsesdialog før en rett slettes
    private fun showDeleteConfirmationDialog(dish: Dish) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_dish_confirm_title))
            .setMessage(getString(R.string.delete_dish_confirm_message, dish.name))
            .setPositiveButton(R.string.yes) { dialog, _ ->
                viewModel.deleteDish(dish)
                Toast.makeText(this, R.string.dish_deleted_message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}