package com.app.pills.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.pills.db.dao.PillDao
import com.app.pills.db.dao.PillIntakeDao
import com.app.pills.db.dao.ScheduleCardDao
import com.app.pills.db.entity.PillIntakeEntity
import com.app.pills.db.entity.PillIntakeWithDetails
import com.app.pills.model.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatViewModel @Inject constructor(
    private val pillIntakeDao: PillIntakeDao
) : ViewModel() {

    val util = Util()
    val allIntakes: LiveData<List<PillIntakeEntity>> = pillIntakeDao.getAllPillIntakes()
    val todayIntakes: MutableLiveData<List<PillIntakeWithDetails>> =
        MutableLiveData<List<PillIntakeWithDetails>>()

    fun updateTodayPillIntakes() {
        CoroutineScope(Dispatchers.IO).launch {
            val newIntakes = pillIntakeDao.getPillIntakeWithDetails().filter {
                util.longToLocalDateTime(it.pillIntake.time).toLocalDate() == LocalDate.now()
            }
            todayIntakes.postValue(newIntakes)
        }
    }
}

