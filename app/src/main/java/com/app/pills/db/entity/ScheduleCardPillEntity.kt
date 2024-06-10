package com.app.pills.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_card_pills",
    foreignKeys = [ForeignKey(entity = ScheduleCardEntity::class, parentColumns = ["id"], childColumns = ["schedule_card_id"], onDelete = ForeignKey.CASCADE)]
)
data class ScheduleCardPillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "schedule_card_id")
    val scheduleCardId: Int,
    @ColumnInfo(name = "pill_name")
    val pillName: String,
    @ColumnInfo(name = "pill_unit_type")
    val pillUnitType: String,
    @ColumnInfo(name = "pill_count")
    val pillCount: Int,
    @ColumnInfo(name = "dosage")
    val dosage: Int
)
