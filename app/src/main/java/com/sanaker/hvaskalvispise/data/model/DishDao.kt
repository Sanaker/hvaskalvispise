package com.sanaker.hvaskalvispise.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {

    // V V V V V MODIFICATION IS HERE V V V V V
    @Query("SELECT * FROM dishes ORDER BY name ASC") // Added "ORDER BY name ASC"
    // ^ ^ ^ ^ ^ MODIFICATION IS HERE ^ ^ ^ ^ ^
    fun getAllDishes(): Flow<List<Dish>>

    @Query("SELECT * FROM dishes WHERE id = :id")
    suspend fun getDishById(id: Long): Dish?

    @Query("SELECT * FROM dishes WHERE name = :name COLLATE NOCASE LIMIT 1") // COLLATE NOCASE for case-insensitive
    suspend fun getDishByName(name: String): Dish?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDish(dish: Dish)

    @Update
    suspend fun updateDish(dish: Dish)

    @Delete
    suspend fun deleteDish(dish: Dish)

    @Query("DELETE FROM dishes")
    suspend fun deleteAllDishes()


}
