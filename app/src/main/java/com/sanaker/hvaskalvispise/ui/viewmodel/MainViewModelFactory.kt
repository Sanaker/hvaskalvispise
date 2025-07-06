package com.sanaker.hvaskalvispise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sanaker.hvaskalvispise.data.model.DishDao // Import DishDao

class MainViewModelFactory(private val dishDao: DishDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Suppress warning for type cast
            return MainViewModel(dishDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}