package com.sanaker.hvaskalvispise.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sanaker.hvaskalvispise.data.model.Dish
import com.sanaker.hvaskalvispise.data.repository.DishRepository
import kotlin.random.Random

class MainViewModel(private val repository: DishRepository) : ViewModel() {

    // LiveData for listen over retter. Observees av UI for å oppdatere seg automatisk.
    private val _dishes = MutableLiveData<MutableList<Dish>>()
    val dishes: LiveData<MutableList<Dish>> = _dishes

    // LiveData for den tilfeldig valgte retten.
    private val _selectedDish = MutableLiveData<Dish?>()
    val selectedDish: LiveData<Dish?> = _selectedDish

    init {
        // Laster retter når ViewModel initialiseres
        loadDishes()
    }

    // Laster retter fra repository og oppdaterer LiveData
    fun loadDishes() {
        _dishes.value = repository.getDishes()
    }

    // Legger til en ny rett
    fun addDish(dishName: String) {
        if (dishName.isNotBlank()) { // Sjekker at navnet ikke er tomt
            val newDish = repository.addDish(dishName)
            // Oppdaterer LiveData, som vil trigge UI-oppdatering
            _dishes.value = _dishes.value?.apply { add(newDish) }
        }
    }

    // Sletter en rett
    fun deleteDish(dish: Dish) {
        repository.deleteDish(dish.id)
        // Oppdaterer LiveData
        _dishes.value = _dishes.value?.apply { remove(dish) }
    }

    // Velger en tilfeldig rett fra listen
    fun chooseRandomDish() {
        val currentDishes = _dishes.value
        if (!currentDishes.isNullOrEmpty()) {
            val randomIndex = Random.nextInt(currentDishes.size)
            _selectedDish.value = currentDishes[randomIndex]
        } else {
            _selectedDish.value = null // Ingen retter å velge fra
        }
    }
}