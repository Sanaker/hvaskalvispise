package com.sanaker.hvaskalvispise.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// Bruker 'data class' sp, automatisk genererer hashCode(), equals(), toString() og copy()
//Dette gjør den ideell for enkle dataklasser
@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Bruker String for ID foreløpig, en UUID kan vøre bra senere
    @ColumnInfo(name = "dish_name")
    val name: String // Navnet på retten
)