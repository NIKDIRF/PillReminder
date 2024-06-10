package com.app.pills.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.app.pills.db.entity.ScheduleCardPillEntity

@Dao
interface ScheduleCardPillDao {
    @Insert
    suspend fun insert(scheduleCardPill: ScheduleCardPillEntity)

    @Insert
    suspend fun insertAll(scheduleCardPills: List<ScheduleCardPillEntity>)

    @Update
    suspend fun update(scheduleCardPill: ScheduleCardPillEntity)

    @Delete
    suspend fun delete(scheduleCardPill: ScheduleCardPillEntity)
}