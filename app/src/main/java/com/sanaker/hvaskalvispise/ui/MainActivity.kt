package com.sanaker.hvaskalvispise.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
// import android.widget.EditText // FJERNES
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts // NY IMPORT
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout // For root layout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton // NY IMPORT
import com.google.android.material.snackbar.Snackbar
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.AppDatabase
import com.sanaker.hvaskalvispise.data.model.Dish
import com.sanaker.hvaskalvispise.ui.addish.AddDishActivity // NY IMPORT (sjekk pakkenavn)
import com.sanaker.hvaskalvispise.ui.viewmodel.AddDishResult
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModel
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var selectedDishTextView: TextView
    private lateinit var chooseDishButton: Button
    // private lateinit var newDishEditText: EditText // FJERNES
    // private lateinit var addDishButton: Button // FJERNES
    private lateinit var viewDishListButton: Button
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainActivityLayout: ConstraintLayout // Endret til ConstraintLayout

    private lateinit var randomDishDetailContainer: LinearLayout
    private lateinit var chipCategory: Chip
    private lateinit var labelIngredients: TextView
    private lateinit var chipGroupIngredients: ChipGroup

    private lateinit var fabAddDish: FloatingActionButton // NYTT for FAB

    // Ny Activity Result Launcher for AddDishActivity
    private val addDishLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                val name = it.getStringExtra(AddDishActivity.EXTRA_DISH_NAME)
                val category = it.getStringExtra(AddDishActivity.EXTRA_DISH_CATEGORY) ?: ""
                val ingredients = it.getStringExtra(AddDishActivity.EXTRA_DISH_INGREDIENTS) ?: ""

                if (!name.isNullOrBlank()) {
                    val newDish = Dish(name = name, category = category, ingredients = ingredients)
                    mainViewModel.addDish(newDish)
                    // Snackbar vil vises via addDishResult observer nedenfor
                }
            }
        }
        // Du kan legge til en else if (result.resultCode == Activity.RESULT_CANCELED) for å håndtere avbryt
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityLayout = findViewById(R.id.mainActivityLayout) // Bruk ID fra root layout

        selectedDishTextView = findViewById(R.id.selectedDishTextView)
        chooseDishButton = findViewById(R.id.chooseDishButton)
        // newDishEditText = findViewById(R.id.newDishEditText) // FJERNES
        // addDishButton = findViewById(R.id.addDishButton) // FJERNES
        viewDishListButton = findViewById(R.id.buttonDishList)
        fabAddDish = findViewById(R.id.fabAddDish) // Initialiser FAB

        randomDishDetailContainer = findViewById(R.id.randomDishDetailContainer)
        chipCategory = findViewById(R.id.chipCategory)
        labelIngredients = findViewById(R.id.labelIngredients)
        chipGroupIngredients = findViewById(R.id.chipGroupIngredients)

        val dishDao = AppDatabase.getDatabase(applicationContext).dishDao()
        val viewModelFactory = MainViewModelFactory(dishDao)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        // --- Observatører (uendret, men 'newDishEditText' referanser i 'addDishResult' må fjernes) ---
        mainViewModel.randomlySelectedDish.observe(this) { dish ->
            // ... (din eksisterende logikk her er uendret) ...
            if (dish != null) {
                selectedDishTextView.text = dish.name
                randomDishDetailContainer.visibility = View.VISIBLE

                if (dish.category.isNullOrEmpty()) {
                    chipCategory.text = getString(R.string.category_not_specified)
                } else {
                    chipCategory.text = dish.category
                    chipCategory.visibility = View.VISIBLE
                }

                chipGroupIngredients.removeAllViews()
                if (dish.ingredients.isNullOrEmpty()) {
                    labelIngredients.visibility = View.GONE
                    chipGroupIngredients.visibility = View.GONE
                } else {
                    val ingredientsList = dish.ingredients!!.split(Regex("[,\n\r]+"))
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    if (ingredientsList.isEmpty()) {
                        labelIngredients.visibility = View.GONE
                        chipGroupIngredients.visibility = View.GONE
                    } else {
                        labelIngredients.visibility = View.VISIBLE
                        chipGroupIngredients.visibility = View.VISIBLE
                        for (ingredientName in ingredientsList) {
                            val ingredientChip = Chip(this@MainActivity)
                            ingredientChip.text = ingredientName
                            chipGroupIngredients.addView(ingredientChip)
                        }
                    }
                }
            } else {
                val dishesList = mainViewModel.allDishes.value
                if (dishesList.isNullOrEmpty()) {
                    selectedDishTextView.text = getString(R.string.message_add_some_dishes_first)
                } else {
                    selectedDishTextView.text = getString(R.string.message_no_dish_selected_try_choosing)
                }
                randomDishDetailContainer.visibility = View.GONE
                chipCategory.visibility = View.GONE
                labelIngredients.visibility = View.GONE
                chipGroupIngredients.visibility = View.GONE
            }
        }

        mainViewModel.allDishes.observe(this) { allDishes ->
            // Fjern referanser til newDishEditText.hint her hvis de fantes
            if (allDishes.isNullOrEmpty()) {
                if (mainViewModel.randomlySelectedDish.value == null) {
                    selectedDishTextView.text = getString(R.string.message_add_some_dishes_first)
                    randomDishDetailContainer.visibility = View.GONE
                    chipCategory.visibility = View.GONE
                    labelIngredients.visibility = View.GONE
                    chipGroupIngredients.visibility = View.GONE
                }
            } else {
                if (mainViewModel.randomlySelectedDish.value == null) {
                    selectedDishTextView.text = getString(R.string.message_no_dish_selected_try_choosing)
                    randomDishDetailContainer.visibility = View.GONE
                    chipCategory.visibility = View.GONE
                    labelIngredients.visibility = View.GONE
                    chipGroupIngredients.visibility = View.GONE
                }
            }
        }

        mainViewModel.addDishResult.observe(this) { result ->
            val message: String? = when (result) {
                is AddDishResult.Success -> {
                    // newDishEditText.text.clear() // FJERNES - EditText er ikke her lenger
                    getString(R.string.toast_dish_added_successfully)
                }
                is AddDishResult.Duplicate -> getString(R.string.toast_dish_already_exists)
                is AddDishResult.EmptyName -> getString(R.string.toast_please_enter_dish_name) // Vil nå komme fra AddDishActivity
                null -> null
            }
            message?.let {
                Snackbar.make(mainActivityLayout, it, Snackbar.LENGTH_LONG).show()
                if (result != null) {
                    mainViewModel.onAddDishResultHandled()
                }
            }
        }

        // --- Knappelyttere ---
        fabAddDish.setOnClickListener { // NYTT for FAB
            val intent = Intent(this, AddDishActivity::class.java)
            addDishLauncher.launch(intent)
        }

        chooseDishButton.setOnClickListener {
            mainViewModel.getRandomDish()
        }

        viewDishListButton.setOnClickListener {
            val intent = Intent(this, DishListActivity::class.java)
            startActivity(intent)
        }

        // Sikre at detaljcontaineren og chip-elementer er skjult ved oppstart hvis ingen data
        if (mainViewModel.randomlySelectedDish.value == null) {
            randomDishDetailContainer.visibility = View.GONE
            chipCategory.visibility = View.GONE
            labelIngredients.visibility = View.GONE
            chipGroupIngredients.visibility = View.GONE
        }
    }
}

