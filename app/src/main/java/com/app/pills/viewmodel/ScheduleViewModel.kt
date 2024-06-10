package com.app.pills.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pills.db.dao.PillDao
import com.app.pills.db.dao.PillIntakeDao
import com.app.pills.db.dao.ScheduleCardDao
import com.app.pills.db.entity.PillEntity
import com.app.pills.db.entity.PillIntakeEntity
import com.app.pills.db.entity.PillIntakePillEntity
import com.app.pills.db.entity.PillIntakeWithDetails
import com.app.pills.db.entity.ScheduleCardEntity
import com.app.pills.db.entity.ScheduleCardWithDetails
import com.app.pills.model.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val pillDao: PillDao,
    private val scheduleDao: ScheduleCardDao,
    private val pillIntakeDao: PillIntakeDao
): ViewModel() {

    var count = 0
    val util = Util()
    val allPills: LiveData<List<PillEntity>> = pillDao.getAllPills()
    val scheduleCards: LiveData<List<ScheduleCardEntity>> = scheduleDao.getAllScheduleCards()
    val allScheduleCards  = MutableLiveData<List<ScheduleCardWithDetails>>()
    val allIntakes: LiveData<List<PillIntakeEntity>> = pillIntakeDao.getAllPillIntakes()
    val todayIntakes: MutableLiveData<List<PillIntakeWithDetails>> = MutableLiveData<List<PillIntakeWithDetails>>()

    fun updateScheduleCards() {
        CoroutineScope(Dispatchers.IO).launch {
            allScheduleCards.postValue(scheduleDao.getScheduleCardWithDetails())
//            if (count == 0) {
//                createNewIntakes()
//                count++
//            }
        }

    }

    fun updateTodayPillIntakes() {
        CoroutineScope(Dispatchers.IO).launch {

            val newIntakes = pillIntakeDao.getPillIntakeWithDetails().filter { util.longToLocalDateTime(it.pillIntake.time).toLocalDate() == LocalDate.now() }

            if (newIntakes.isEmpty()) {
                Log.d("TO CREATE", "TRUE")

            } else {
                Log.d("TO CREATE", "FALSE")
                todayIntakes.postValue(newIntakes)
            }
        }
    }

    fun deleteSchedule(scheduleCard: ScheduleCardEntity) {
        viewModelScope.launch {
            scheduleDao.delete(scheduleCard)
        }
    }

    fun updateIntake(pillIntakeEntity: PillIntakeEntity) {
        viewModelScope.launch {
            pillIntakeDao.update(pillIntakeEntity)
            Log.d("PillIntakeEntity UPDATE", "$pillIntakeEntity")
        }
    }

    private fun updatePillIntakes() {
        viewModelScope.launch {
            todayIntakes.value = pillIntakeDao.getPillIntakeWithDetails()
        }
    }

    private fun deleteIntake(pillIntakeEntity: PillIntakeEntity) {
        viewModelScope.launch {
            pillIntakeDao.delete(pillIntakeEntity)
        }
    }


    suspend fun getIntake(intakeId: Int): PillIntakeWithDetails? {
        return pillIntakeDao.getPillIntakeById(intakeId)
    }

    suspend fun updatePillByName(name: String, dosage: Int) {
        return pillDao.updatePillByName(name, dosage)
    }

    fun getPillCountByName(name: String): Int {
        return pillDao.getPillCountByName(name)?: 0
    }


    fun createNewIntakes() {
        CoroutineScope(Dispatchers.IO).launch {

            val save: MutableList<PillIntakeEntity> = mutableListOf<PillIntakeEntity>()
            allIntakes.value?.filter { util.longToLocalDateTime(it.time).toLocalDate() == LocalDate.now()}?.forEach {
                if (it.isDone == 0) pillIntakeDao.delete(it) else save.add(it)
            }
            Log.d("SAVE", "$save")

            allIntakes.value?.forEach {
                if (util.longToLocalDateTime(it.time).toLocalDate() == LocalDate.now() && !save.contains(it)) {
                    deleteIntake(it)
                }
            }

            val today = LocalDate.now()
            val dayOfWeek = today.dayOfWeek.value - 1
            Log.d("CREATE", "${allScheduleCards.value}")
            val todayScheduleCards = allScheduleCards.value?.filter {
                util.longToLocalDateTime(it.scheduleCard.firstDate).toLocalDate().minusDays(1)
                    .isBefore(today)
                        && util.longToLocalDateTime(it.scheduleCard.lastDate).toLocalDate()
                    .plusDays(1).isAfter(today)
                        && (((it.scheduleCard.daysOfWeek and (1 shl dayOfWeek)) shr dayOfWeek) == 1)
            }?: listOf()

            for (schedule in todayScheduleCards) {

                for (timeSlot in schedule.timeSlots) {
                    val localDateTime = util.localDateTimeToLong(LocalDateTime.of(LocalDate.now(), util.longToLocalTime(timeSlot.time)))
                    val intake = PillIntakeEntity(time = localDateTime, isDone = 0)
                    val intakeId = pillIntakeDao.insert(intake).toInt()

                    for (pill in schedule.pills) {
                        pillIntakeDao.insertPillIntakePill(
                            PillIntakePillEntity(
                                pillIntakeId = intakeId,
                                pillName = pill.pillName,
                                pillUnitType = pill.pillUnitType,
                                pillCount = pill.pillCount,
                                dosage = pill.dosage
                            )
                        )
                    }

                    val newIntake = getIntake(intakeId)
                    Log.d("CHECK NEW", "${newIntake?.pillIntake}")
                    for (old in save) {
                        val oldIntake = getIntake(old.id!!)
                        if (oldIntake!!.pills.size == newIntake!!.pills.size && oldIntake.pills.isNotEmpty()) {
                            val oldPills = oldIntake.pills
                            val newPills = newIntake.pills
                            val equal = oldPills.zip(newPills).all { (oldPill, newPill) ->
                                oldPill.pillName == newPill.pillName &&
                                oldPill.pillUnitType == newPill.pillUnitType &&
                                oldPill.dosage == newPill.dosage
                            }

                            if (equal) {
                                Log.d("DELETE NEW", "${newIntake.pillIntake}")
                                deleteIntake(newIntake.pillIntake)
                            }
                        }
                    }

                }
            }
        }
    }

}