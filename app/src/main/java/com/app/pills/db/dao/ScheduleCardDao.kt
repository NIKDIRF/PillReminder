package com.app.pills.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.pills.db.entity.ScheduleCardEntity
import com.app.pills.db.entity.ScheduleCardPillEntity
import com.app.pills.db.entity.ScheduleCardTimeSlotEntity
import com.app.pills.db.entity.ScheduleCardWithDetails

@Dao
interface ScheduleCardDao {
    @Insert
    suspend fun insert(scheduleCard: ScheduleCardEntity): Long

    @Insert
    suspend fun insertScheduleCardPills(pills: List<ScheduleCardPillEntity>)

    @Insert
    suspend fun insertScheduleCardTimeSlots(timeSlots: List<ScheduleCardTimeSlotEntity>)

    @Update
    suspend fun update(scheduleCard: ScheduleCardEntity)

    @Delete
    suspend fun delete(scheduleCard: ScheduleCardEntity)

    @Query("SELECT * FROM schedule_cards")
    fun getAllScheduleCards(): LiveData<List<ScheduleCardEntity>>

    @Transaction
    @Query("SELECT * FROM schedule_cards")
    suspend fun getScheduleCardWithDetails(): List<ScheduleCardWithDetails>
}