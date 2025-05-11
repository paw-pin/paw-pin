package com.example.pawpin_v2.data

import android.content.Context
import androidx.room.Room

private var INSTANCE: AppDatabase? = null

fun getDatabase(context: Context): AppDatabase {
    return INSTANCE ?: synchronized(AppDatabase::class.java) {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "walks_database"
        ).fallbackToDestructiveMigration().build().also {
            INSTANCE = it
        }
    }
}
