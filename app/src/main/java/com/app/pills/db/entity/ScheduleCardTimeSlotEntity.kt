package com.app.pills.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_card_time_slots",
    foreignKeys = [ForeignKey(entity = ScheduleCardEntity::class, parentColumns = ["id"], childColumns = ["schedule_card_id"], onDelete = ForeignKey.CASCADE)]
)
data class ScheduleCardTimeSlotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "schedule_card_id")
    val scheduleCardId: Int,
    @ColumnInfo(name = "time")
    val time: Long
)
