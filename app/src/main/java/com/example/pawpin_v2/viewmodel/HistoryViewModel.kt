package com.example.pawpin_v2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawpin_v2.data.AppDatabase
import com.example.pawpin_v2.data.Walk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val walkDao = AppDatabase.getDatabase(application).walkDao()

    val walkHistoryFlow: Flow<List<Walk>> = walkDao.getAllWalks()

    fun deleteWalk(walk: Walk) {
        viewModelScope.launch {
            // If you add delete functionality later
            // walkDao.deleteWalk(walk)
        }
    }
}
