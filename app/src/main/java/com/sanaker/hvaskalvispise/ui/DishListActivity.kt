package com.sanaker.hvaskalvispise.ui

import android.os.Bundle
import android.util.Log // <--- NY IMPORT FOR LOGGING
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar // <--- NY IMPORT FOR TOOLBAR
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sanaker.hvaskalvispise.R
import com.sanaker.hvaskalvispise.data.model.AppDatabase
import com.sanaker.hvaskalvispise.data.model.Dish
import com.sanaker.hvaskalvispise.ui.adapter.DishAdapter
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModel
import com.sanaker.hvaskalvispise.ui.viewmodel.MainViewModelFactory


class DishListActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var dishAdapter: DishAdapter
    private lateinit var recyclerViewDishes: RecyclerView
    private lateinit var textViewEmptyDishList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish_list)

        // -------- V V V V TOOLBAR OPPSETT V V V V --------
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        Log.d("DishListActivity", "Toolbar findViewById successful. ID: ${toolbar.id}")
        setSupportActionBar(toolbar)
        Log.d("DishListActivity", "setSupportActionBar CALLED")
        supportActionBar?.title = "Matrettliste" // Sett tittel for Action Bar
        // -------- ^ ^ ^ ^ TOOLBAR OPPSETT ^ ^ ^ ^ --------

        val dishDao = AppDatabase.getDatabase(applicationContext).dishDao()
        val viewModelFactory = MainViewModelFactory(dishDao)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        recyclerViewDishes = findViewById(R.id.recyclerViewDishes)
        textViewEmptyDishList = findViewById(R.id.textViewEmptyDishList)

        recyclerViewDishes.layoutManager = LinearLayoutManager(this)

        dishAdapter = DishAdapter(
            onDeleteClicked = { dishToDelete, position ->
                viewModel.onDishDeleteRequested(dishToDelete, position)
            },
            onEditButtonClicked = { dish -> showEditDishDialog(dish) },
            onItemClicked = { dishToEdit ->
                showEditDishDialog(dishToEdit)
            }
        )
        recyclerViewDishes.adapter = dishAdapter

        viewModel.uiVisibleDishes.observe(this) { dishes ->
            if (dishes.isNullOrEmpty()) {
                recyclerViewDishes.visibility = View.GONE
                textViewEmptyDishList.visibility = View.VISIBLE
            } else {
                recyclerViewDishes.visibility = View.VISIBLE
                textViewEmptyDishList.visibility = View.GONE
                dishAdapter.submitList(dishes)
            }
        }


        viewModel.showUndoSnackbar.observe(this) { dish -> // dish er her direkte av typen Dish (eller kan være null)
            dish?.let { nonNullDish ->
                Snackbar.make(recyclerViewDishes, getString(R.string.dish_deleted_snackbar, nonNullDish.name), Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo_snackbar) {
                        viewModel.undoDeleteDish()
                    }
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE) {
                                viewModel.confirmDeleteDish()
                            }
                        }
                    })
                    .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d("DishListActivity", "onCreateOptionsMenu CALLED")
        menuInflater.inflate(R.menu.dish_list_menu, menu)
        Log.d("DishListActivity", "Menu inflated. Item count: ${menu.size()}")
        if (menu.size() == 0) {
            Log.e("DishListActivity", "MENU IS EMPTY AFTER INFLATE! Check dish_list_menu.xml and R.menu.dish_list_menu reference.")
        }
        // For feilsøking, prøv å legge til et menyvalg programmatisk:
        // menu.add(0, 12345, 0, "Test Menyvalg").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        // Log.d("DishListActivity", "Programmatic menu item added. New count: ${menu.size()}")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("DishListActivity", "onOptionsItemSelected CALLED for item: ${item.title}")
        return when (item.itemId) {
            R.id.action_clear_all_dishes -> {
                Log.d("DishListActivity", "action_clear_all_dishes SELECTED")
                showClearAllConfirmationDialog()
                true
            }
            // 12345 -> { // Hvis du tester med programmatisk menyvalg
            //     Log.d("DishListActivity", "Test Menyvalg SELECTED")
            //     Toast.makeText(this, "Test Menyvalg trykket!", Toast.LENGTH_SHORT).show()
            //     true
            // }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showClearAllConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_clear_all_dishes)
            .setMessage(R.string.dialog_message_clear_all_dishes)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.action_delete_all) { _, _ ->
                viewModel.clearAllDishes()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showEditDishDialog(dish: Dish) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Dish") // Endre tittel til noe mer generelt

        // Opprett et LinearLayout for å holde alle input-feltene
        val layoutContainer = LinearLayout(this)
        layoutContainer.orientation = LinearLayout.VERTICAL
        // Legg til litt padding rundt hele containeren
        val generalPadding = (16 * resources.displayMetrics.density).toInt() // ca 16dp
        layoutContainer.setPadding(generalPadding, generalPadding / 2, generalPadding, generalPadding / 2)

        // Standard layout params for EditText-feltene
        val editTextLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val verticalMargin = (8 * resources.displayMetrics.density).toInt() // ca 8dp
        editTextLayoutParams.setMargins(0, verticalMargin / 2, 0, verticalMargin / 2)


        // Input for Name
        val inputName = EditText(this).apply {
            setText(dish.name)
            hint = "Name"
            isSingleLine = true
            layoutParams = editTextLayoutParams
        }
        layoutContainer.addView(inputName)

        // Input for Category
        val inputCategory = EditText(this).apply {
            setText(dish.category ?: "")
            hint = "Category (e.g., Dinner, Lunch)"
            isSingleLine = true
            layoutParams = editTextLayoutParams
        }
        layoutContainer.addView(inputCategory)

        // Input for Ingredients
        val inputIngredients = EditText(this).apply {
            setText(dish.ingredients ?: "")
            hint = "Ingredients (one per line or comma separated)"
            // minLines = 2 // Valgfritt: gjør feltet litt høyere
            layoutParams = editTextLayoutParams
        }
        layoutContainer.addView(inputIngredients)

        // Input for Instructions
        val inputInstructions = EditText(this).apply {
            setText(dish.instructions ?: "")
            hint = "Instructions"
            // minLines = 3 // Valgfritt: gjør feltet litt høyere
            layoutParams = editTextLayoutParams
        }
        layoutContainer.addView(inputInstructions)

        // CheckBox for Favorite status
        val isFavoriteCheckBox = CheckBox(this).apply {
            text = "Mark as Favorite"
            isChecked = dish.isFavorite
            layoutParams = editTextLayoutParams
        }
        layoutContainer.addView(isFavoriteCheckBox)

        builder.setView(layoutContainer) // Sett LinearLayout som view for dialogen

        builder.setPositiveButton("Save") { _, _ ->
            val newName = inputName.text.toString().trim()
            // For nullable felt, sett til null hvis tom, ellers trimmet verdi
            val newCategory = inputCategory.text.toString().trim().let { if (it.isEmpty()) null else it }
            val newIngredients = inputIngredients.text.toString().trim().let { if (it.isEmpty()) null else it }
            val newInstructions = inputInstructions.text.toString().trim().let { if (it.isEmpty()) null else it }
            val newIsFavorite = isFavoriteCheckBox.isChecked

            if (newName.isNotEmpty()) {
                val updatedDish = dish.copy(
                    name = newName,
                    category = newCategory,
                    ingredients = newIngredients,
                    instructions = newInstructions,
                    isFavorite = newIsFavorite
                    // id og creationDate beholdes fra den originale 'dish' via copy()
                )

                if (updatedDish != dish) { // Bare oppdater hvis noe faktisk er endret
                    viewModel.updateDish(updatedDish)
                    Toast.makeText(this, "Dish updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No changes were made.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Dish name cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}
