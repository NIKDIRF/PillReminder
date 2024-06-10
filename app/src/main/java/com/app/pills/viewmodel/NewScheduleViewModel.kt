package com.app.pills.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pills.db.dao.PillDao
import com.app.pills.db.dao.PillIntakeDao
import com.app.pills.db.dao.ScheduleCardDao
import com.app.pills.db.dao.ScheduleCardPillDao
import com.app.pills.db.dao.ScheduleCardTimeSlotDao
import com.app.pills.db.entity.PillEntity
import com.app.pills.db.entity.ScheduleCardEntity
import com.app.pills.db.entity.ScheduleCardPillEntity
import com.app.pills.db.entity.ScheduleCardTimeSlotEntity
import com.app.pills.model.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class NewScheduleViewModel @Inject constructor(
    private val pillDao: PillDao,
    private val scheduleCardDao: ScheduleCardDao,
    private val scheduleCardPillDao: ScheduleCardPillDao,
    private val scheduleCardTimeSlotDao: ScheduleCardTimeSlotDao,
    private val pillIntakeDao: PillIntakeDao
) : ViewModel() {

    val allPills: LiveData<List<PillEntity>> = pillDao.getAllPills()
    val startDate = MutableLiveData<Long>()
    val endDate = MutableLiveData<Long>()
    val week = MutableLiveData<Int>()
    val scheduleCardTimeSlots = MutableLiveData<MutableList<ScheduleCardTimeSlotEntity>>(mutableListOf())
    val scheduleCardPills = MutableLiveData<MutableList<ScheduleCardPillEntity>>()
    private val util = Util()

    fun setWeek(newWeek: Int) {
        week.value = newWeek
    }

    fun setStartDate(newStartDate: LocalDateTime) {
        startDate.value = util.localDateTimeToLong(newStartDate)
    }

    fun setEndDate(newEndDate: LocalDateTime) {
        endDate.value = util.localDateTimeToLong(newEndDate)
    }

    fun setScheduleCardPills() {
        scheduleCardPills.value = toScheduleCardPillEntity()
    }

    fun addPillIntake(scheduleCardTimeSlot: ScheduleCardTimeSlotEntity) {
        val newScheduleCardTimeSlots = scheduleCardTimeSlots.value ?: mutableListOf()
        if (!scheduleCardTimeSlots.value!!.any { it.time == scheduleCardTimeSlot.time }) {
            newScheduleCardTimeSlots.add(scheduleCardTimeSlot)
            newScheduleCardTimeSlots.sortBy { it.time }
            scheduleCardTimeSlots.value = newScheduleCardTimeSlots
            Log.d("addPillIntake", "$newScheduleCardTimeSlots")
        }
    }

    fun removePillIntake(scheduleCardTimeSlot: ScheduleCardTimeSlotEntity) {
        val newScheduleCardTimeSlot = scheduleCardTimeSlots.value ?: mutableListOf()
        newScheduleCardTimeSlot.remove(scheduleCardTimeSlot)
        newScheduleCardTimeSlot.sortBy { it.time }
        scheduleCardTimeSlots.value = newScheduleCardTimeSlot
    }

    fun toScheduleCardPillEntity(): MutableList<ScheduleCardPillEntity> {

        val pillList = allPills.value

        return pillList?.map { pillEntity ->
            ScheduleCardPillEntity(
                scheduleCardId = -1,
                pillName = pillEntity.name,
                pillUnitType = pillEntity.unitType,
                pillCount = pillEntity.count,
                dosage = 0
            )
        }?.toMutableList() ?: mutableListOf()
    }

    fun updateDosage(pillName: String, newDosage: Int) {
        val currentPills = scheduleCardPills.value ?: return
        val updatedPills = currentPills.map { pill ->
            if (pill.pillName == pillName) {
                pill.copy(dosage = newDosage)
            } else {
                pill
            }
        }.toMutableList()
        Log.d("updateDosage", updatedPills.toString())
        scheduleCardPills.value = updatedPills
    }

    fun addNewScheduleCard() {
        CoroutineScope(Dispatchers.IO).launch {
            val newStartDate = startDate.value!!
            val newEndDate = endDate.value!!
            val newWeek = week.value!!

            val newScheduleCard = ScheduleCardEntity(
                firstDate = newStartDate,
                lastDate = newEndDate,
                daysOfWeek = newWeek
            )

            val scheduleCardId = scheduleCardDao.insert(newScheduleCard).toInt()

            scheduleCardTimeSlots.value?.map { timeSlot ->
                timeSlot.copy(scheduleCardId = scheduleCardId)
            }?.let { timeSlots ->
                Log.d("DB TIMESLOT", "$timeSlots")
                scheduleCardTimeSlotDao.insertAll(timeSlots)
            }

            scheduleCardPills.value?.filter { it.dosage > 0 }?.map { pill ->
                pill.copy(scheduleCardId = scheduleCardId)
            }?.let { pills ->
                Log.d("DB PILLS", "$pills")
                scheduleCardPillDao.insertAll(pills)
            }
        }
    }
}

