package com.sanaker.hvaskalvispise.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                                    // Autogenerert ID
    var name: String,                                   // Navn på matrettet
    var category : String? = null,                      // Kategori er valgfritt
    var ingredients : String? = null,                   // Ingredienser er valgfritt
    var instructions : String? = null,                  // Instruksjoner er valgfritt
    var isFavorite : Boolean = false,                   // Favorittstatus
    var creationDate: Long = System.currentTimeMillis() // Tidspunkt for opprettelsen

)