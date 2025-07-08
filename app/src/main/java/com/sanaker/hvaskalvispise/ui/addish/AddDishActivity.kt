package com.sanaker.hvaskalvispise.ui.addish // Dobbeltsjekk at dette pakkenavnet er korrekt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.sanaker.hvaskalvispise.R

class AddDishActivity : AppCompatActivity() {

    private lateinit var editTextDishName: TextInputEditText
    private lateinit var editTextDishCategory: TextInputEditText
    private lateinit var chipGroupIngredients: ChipGroup
    private lateinit var editTextNewIngredient: TextInputEditText
    private lateinit var buttonAddIngredient: ImageButton
    private lateinit var buttonSaveDish: Button
    private lateinit var buttonCancel: Button

    private val currentIngredients = mutableListOf<String>()

    companion object {
        const val EXTRA_DISH_NAME = "com.sanaker.hvaskalvispise.EXTRA_DISH_NAME"
        const val EXTRA_DISH_CATEGORY = "com.sanaker.hvaskalvispise.EXTRA_DISH_CATEGORY"
        const val EXTRA_DISH_INGREDIENTS = "com.sanaker.hvaskalvispise.EXTRA_DISH_INGREDIENTS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dish)

        // Støtte for "Tilbake"-pil i ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_activity_add_dish) // Sett tittel


        editTextDishName = findViewById(R.id.editTextDishName)
        editTextDishCategory = findViewById(R.id.editTextDishCategory)
        chipGroupIngredients = findViewById(R.id.chipGroupIngredients)
        editTextNewIngredient = findViewById(R.id.editTextNewIngredient)
        buttonAddIngredient = findViewById(R.id.buttonAddIngredient)
        buttonSaveDish = findViewById(R.id.buttonSaveDish)
        buttonCancel = findViewById(R.id.buttonCancel)

        buttonAddIngredient.setOnClickListener {
            addIngredientToListAndChipGroup()
        }

        editTextNewIngredient.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addIngredientToListAndChipGroup()
                // Return true to indicate that the event has been consumed
                true
            } else {
                false
            }
        }

        buttonSaveDish.setOnClickListener {
            saveDish()
        }

        buttonCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Håndterer Tilbake-pilen i ActionBar, fungerer som "Avbryt"
        setResult(Activity.RESULT_CANCELED)
        finish()
        return true
    }

    private fun addIngredientToListAndChipGroup() {
        val ingredientName = editTextNewIngredient.text.toString().trim()
        if (ingredientName.isNotEmpty()) {
            if (!currentIngredients.any { it.equals(ingredientName, ignoreCase = true) }) { // Unngå duplikater (ignorer store/små bokstaver)
                currentIngredients.add(ingredientName)
                addChipToGroup(ingredientName)
                editTextNewIngredient.text?.clear()
            } else {
                Toast.makeText(this, getString(R.string.toast_ingredient_added, ingredientName), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_enter_ingredient), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addChipToGroup(ingredient: String) {
        val chip = Chip(this).apply {
            text = ingredient
            isCloseIconVisible = true
            // chip.closeIcon = ContextCompat.getDrawable(this@AddDishActivity, R.drawable.ic_close_custom) // Hvis du vil ha et custom ikon
            setOnCloseIconClickListener {
                chipGroupIngredients.removeView(this) // 'this' her refererer til Chip-objektet
                currentIngredients.remove(ingredient)
            }
        }
        chipGroupIngredients.addView(chip)
    }

    private fun saveDish() {
        val dishName = editTextDishName.text.toString().trim()
        val category = editTextDishCategory.text.toString().trim()
        val ingredientsString = if (currentIngredients.isEmpty()) "" else currentIngredients.joinToString("\n")

        if (dishName.isEmpty()) {
            editTextDishName.error = getString(R.string.error_dish_name_required)
            // Toast.makeText(this, getString(R.string.error_dish_name_required), Toast.LENGTH_SHORT).show() // Valgfritt, error på feltet er ofte nok
            return
        }

        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_DISH_NAME, dishName)
        resultIntent.putExtra(EXTRA_DISH_CATEGORY, category)
        resultIntent.putExtra(EXTRA_DISH_INGREDIENTS, ingredientsString)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
