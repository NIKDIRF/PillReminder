package com.app.pills.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ScheduleCardWithDetails(
    @Embedded val scheduleCard: ScheduleCardEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "schedule_card_id"
    )
    val pills: List<ScheduleCardPillEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "schedule_card_id"
    )
    val timeSlots: List<ScheduleCardTimeSlotEntity>
)
