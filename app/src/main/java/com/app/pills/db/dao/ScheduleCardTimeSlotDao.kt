package com.app.pills.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.app.pills.db.entity.ScheduleCardTimeSlotEntity

@Dao
interface ScheduleCardTimeSlotDao {
    @Insert
    suspend fun insert(scheduleCardTimeSlot: ScheduleCardTimeSlotEntity)

    @Insert
    suspend fun insertAll(scheduleCardTimeSlots: List<ScheduleCardTimeSlotEntity>)

    @Update
    suspend fun update(scheduleCardTimeSlot: ScheduleCardTimeSlotEntity)

    @Delete
    suspend fun delete(scheduleCardTimeSlot: ScheduleCardTimeSlotEntity)
}