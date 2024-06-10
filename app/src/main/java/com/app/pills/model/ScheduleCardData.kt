package com.app.pills.model

import com.app.pills.db.entity.PillEntity
import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduleCardData(
    val pillList: List<Pair<PillEntity, Int>>,
    val timeSlots: List<LocalTime>,
    val daysOfWeek: Int,
    val firstDat: LocalDateTime,
    val lastDat: LocalDateTime
)