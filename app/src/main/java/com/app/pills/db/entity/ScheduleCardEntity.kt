package com.app.pills.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_cards")
data class ScheduleCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "days_of_week")
    val daysOfWeek: Int,
    @ColumnInfo(name = "first_date")
    val firstDate: Long,
    @ColumnInfo(name = "last_date")
    val lastDate: Long
)
