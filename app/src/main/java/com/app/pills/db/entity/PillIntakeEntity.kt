package com.app.pills.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pill_intakes")
data class PillIntakeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "is_done")
    val isDone: Int
)
