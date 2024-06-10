package com.app.pills.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pill_intake_pills",
    foreignKeys = [ForeignKey(entity = PillIntakeEntity::class, parentColumns = ["id"], childColumns = ["pill_intake_id"], onDelete = ForeignKey.CASCADE)]
)
data class PillIntakePillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "pill_intake_id")
    val pillIntakeId: Int,
    @ColumnInfo(name = "pill_name")
    val pillName: String,
    @ColumnInfo(name = "pill_count")
    val pillCount: Int,
    @ColumnInfo(name = "pill_unit_type")
    val pillUnitType: String,
    @ColumnInfo(name = "dosage")
    val dosage: Int
)
