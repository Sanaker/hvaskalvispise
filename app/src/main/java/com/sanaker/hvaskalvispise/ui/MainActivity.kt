package com.sanaker.hvaskalvispise.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout // <-- NY IMPORT
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.AppDatabase
import com.sanaker.hvaskalvispise.data.model.Dish // <-- NY IMPORT (hvis ikke allerede der)
import com.sanaker.hvaskalvispise.ui.viewmodel.AddDishResult
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModel
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var selectedDishTextView: TextView
    private lateinit var chooseDishButton: Button
    private lateinit var newDishEditText: EditText
    private lateinit var addDishButton: Button
    private lateinit var viewDishListButton: Button // Endret navn til viewDishListButton for konsistens
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainActivityLayout: View

    // NYTT: Referanser til detaljvisningselementene fra activity_main.xml
    private lateinit var randomDishDetailContainer: LinearLayout
    private lateinit var randomDishCategoryTextView: TextView
    private lateinit var randomDishIngredientsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityLayout = findViewById(android.R.id.content)

        selectedDishTextView = findViewById(R.id.selectedDishTextView)
        chooseDishButton = findViewById(R.id.chooseDishButton)
        newDishEditText = findViewById(R.id.newDishEditText)
        addDishButton = findViewById(R.id.addDishButton)
        viewDishListButton = findViewById(R.id.buttonDishList) // Bruker korrigert ID

        // NYTT: Initialiser referanser til de nye UI-elementene
        randomDishDetailContainer = findViewById(R.id.randomDishDetailContainer)
        randomDishCategoryTextView = findViewById(R.id.randomDishCategoryTextView)
        randomDishIngredientsTextView = findViewById(R.id.randomDishIngredientsTextView)

        val dishDao = AppDatabase.getDatabase(applicationContext).dishDao()
        val viewModelFactory = MainViewModelFactory(dishDao)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        // --- Start: Oppdatert logikk for visning av tilfeldig rett ---
        mainViewModel.randomlySelectedDish.observe(this) { dish -> // Observer den NYE LiveData-en
            if (dish != null) {
                selectedDishTextView.text = dish.name // Viser navnet som før

                // Vis kategori
                randomDishCategoryTextView.text = if (dish.category.isNullOrEmpty()) {
                    getString(R.string.category_not_specified) // Bruk string resource
                } else {
                    getString(R.string.category_prefix, dish.category) // Bruk string resource med placeholder
                }

                // Vis ingredienser
                randomDishIngredientsTextView.text = if (dish.ingredients.isNullOrEmpty()) {
                    getString(R.string.ingredients_not_specified) // Bruk string resource
                } else {
                    getString(R.string.ingredients_prefix, dish.ingredients) // Bruk string resource med placeholder
                }
                randomDishDetailContainer.visibility = View.VISIBLE
            } else {
                // Håndter tilfellet der ingen rett er valgt eller listen er tom
                val dishesList = mainViewModel.allDishes.value // Sjekk den fulle listen
                if (dishesList.isNullOrEmpty()) {
                    selectedDishTextView.text = getString(R.string.message_add_some_dishes_first)
                } else {
                    selectedDishTextView.text = getString(R.string.message_no_dish_selected_try_choosing)
                }
                randomDishDetailContainer.visibility = View.GONE
            }
        }
        // --- Slutt: Oppdatert logikk for visning av tilfeldig rett ---

        // Observer for den totale listen over matretter (for hint i EditText)
        mainViewModel.allDishes.observe(this) { allDishes -> // Endret til allDishes for klarhet
            if (allDishes.isNullOrEmpty()) {
                newDishEditText.hint = getString(R.string.hint_add_your_first_dish)
                // Hvis listen er tom og ingen rett er valgt av randomlySelectedDish, oppdater hovedteksten
                if (mainViewModel.randomlySelectedDish.value == null) {
                    selectedDishTextView.text = getString(R.string.message_add_some_dishes_first)
                    randomDishDetailContainer.visibility = View.GONE // Skjul også detaljer
                }
            } else {
                newDishEditText.hint = getString(R.string.hint_add_dish_default)
                // Hvis listen IKKE er tom, men ingen rett er valgt av randomlySelectedDish ennå
                if (mainViewModel.randomlySelectedDish.value == null) {
                    selectedDishTextView.text = getString(R.string.message_no_dish_selected_try_choosing)
                    randomDishDetailContainer.visibility = View.GONE // Skjul også detaljer
                }
            }
        }

        mainViewModel.addDishResult.observe(this) { result ->
            val message: String? = when (result) {
                is AddDishResult.Success -> {
                    newDishEditText.text.clear()
                    getString(R.string.toast_dish_added_successfully)
                }
                is AddDishResult.Duplicate -> getString(R.string.toast_dish_already_exists)
                is AddDishResult.EmptyName -> getString(R.string.toast_please_enter_dish_name)
                null -> null
            }

            if (message != null) {
                Snackbar.make(mainActivityLayout, message, Snackbar.LENGTH_LONG).show()
            }

            if (result != null) {
                mainViewModel.onAddDishResultHandled()
            }
        }

        addDishButton.setOnClickListener {
            val dishName = newDishEditText.text.toString().trim()
            // Viktig: Nå må vi sende et Dish-objekt til addDish.
            // For MainActivity legger vi kun til med navn, de andre feltene blir null/default.
            // Hvis du vil legge til flere felt her, må du legge til EditTexts for dem i layouten.
            if (dishName.isNotEmpty()) { // Sjekk om navnet er tomt flyttet hit for AddDishResult.EmptyName
                mainViewModel.addDish(Dish(name = dishName))
            } else {
                mainViewModel.addDish(Dish(name = "")) // Send tom for å trigge AddDishResult.EmptyName
            }
        }

        chooseDishButton.setOnClickListener {
            mainViewModel.getRandomDish() // Kall den NYE funksjonen i ViewModel
        }

        viewDishListButton.setOnClickListener {
            val intent = Intent(this, DishListActivity::class.java)
            startActivity(intent)
        }

        // Sikre at detaljcontaineren er skjult ved oppstart
        randomDishDetailContainer.visibility = View.GONE
    }
}
