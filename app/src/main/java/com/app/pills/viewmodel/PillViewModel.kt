package com.app.pills.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pills.db.dao.PillDao
import com.app.pills.db.entity.PillEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PillViewModel @Inject constructor(
    private val pillDao: PillDao,
) : ViewModel() {

    val allPills: LiveData<List<PillEntity>> = pillDao.getAllPills()

    fun insertPill(pill: PillEntity) {
        viewModelScope.launch {
            pillDao.insertPill(pill)
        }
    }

    fun getPillById(id: Int): PillEntity {

        return pillDao.getPillById(id)
    }

    fun updatePill(pill: PillEntity) {
        viewModelScope.launch {
            pillDao.updatePill(pill)
        }
    }

    fun deletePill(pill: PillEntity) {
        viewModelScope.launch {
            pillDao.deletePill(pill)
        }
    }
}