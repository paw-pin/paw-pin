package com.example.pawpin_v2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Walk::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walkDao(): WalkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "walk_database"  // VERY IMPORTANT (SAME NAME EVERYWHERE)
                )
                    .fallbackToDestructiveMigration() //  MUST have this to avoid schema mismatch
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
