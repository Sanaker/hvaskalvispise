package com.sanaker.hvaskalvispise.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanaker.hvaskalvispise.data.model.Dish
import com.sanaker.hvaskalvispise.util.Constants
import java.util.UUID // For å generere unike ID-er

class DishRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    // Henter alle retter fra SharedPrefrences
    fun getDishes(): MutableList<Dish> {
        val json = sharedPreferences.getString(Constants.KEY_DISH_LIST, null)
        return if (json == null) {
            mutableListOf()
        } else {
            // Bruker Gson for å konvertere JSON-strengen til en liste av Dish-objekter
            val type = object : TypeToken<MutableList<Dish>>() {}.type
            gson.fromJson(json, type)
        }
    }

    // Lagrer listen over retter til SharedPreferences
    fun saveDishes(dishes: List<Dish>) {
        // Konverterer listen av Dish-objekter til en JSON-streng
        val json = gson.toJson(dishes)
        sharedPreferences.edit().putString(Constants.KEY_DISH_LIST, json).apply()
    }

    // Legger til en ny rett og lagrer oppdatert liste
    fun addDish(dishName: String): Dish {
        val currentDishes = getDishes()
        val newDish = Dish(id = UUID.randomUUID().toString(), name = dishName) // Genererer en unik ID
        currentDishes.add(newDish)
        saveDishes(currentDishes)
        return newDish
    }

    // Sletter en rett basert på ID og lagrer oppdatert liste
    fun deleteDish(dishId: String) {
        val currentDishes = getDishes()
        val updatedDishes = currentDishes.filter { it.id != dishId }
        saveDishes(updatedDishes)
    }
}