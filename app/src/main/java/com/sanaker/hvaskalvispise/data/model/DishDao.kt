package com.sanaker.hvaskalvispise.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy // Correct import and casing
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Corrected @Insert and OnConflictStrategy casing
    suspend fun insert(dish: Dish) // Corrected function name and parameter type casing

    @Delete
    suspend fun delete(dish: Dish) // Corrected parameter type casing

    @Query("SELECT * FROM dishes ORDER BY dish_name ASC")
    fun getAllDishes(): Flow<List<Dish>> // Corrected return type casing

    @Query("SELECT * FROM dishes WHERE id = :id")
    suspend fun getDishById(id: Int): Dish?
}