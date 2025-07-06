package com.sanaker.hvaskalvispise.data.model // Adjust package name as needed

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase // Import for callback

// Define the database: entities it contains and its version.
// version should be incremented if you change the database schema.
@Database(entities = [Dish::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Abstract method to get the DAO for Dish operations
    abstract fun dishDao(): DishDao

    companion object {
        @Volatile // Makes the instance immediately visible to other threads
        private var INSTANCE: AppDatabase? = null

        // Singleton pattern to get the database instance
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // Ensures only one thread creates the DB
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context to prevent memory leaks
                    AppDatabase::class.java,
                    "dish_database" // The name of your database file
                )
                    // If you're building a real app, you might want to remove allowMainThreadQueries().
                    // It's here for initial simplicity but can cause ANRs in production.
                    // Instead, use coroutines (which we will).
                    // .allowMainThreadQueries()
                    // Add callback for initial data if needed, or migration strategies
                    // .addCallback(AppDatabaseCallback(scope)) // Will add this if needed later
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}