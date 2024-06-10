package com.app.pills.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PillIntakeWithDetails(
    @Embedded val pillIntake: PillIntakeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pill_intake_id"
    )
    val pills: List<PillIntakePillEntity>
)
