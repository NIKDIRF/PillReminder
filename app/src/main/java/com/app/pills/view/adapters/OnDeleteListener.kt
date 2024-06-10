package com.app.pills.view.adapters

import com.app.pills.db.entity.PillIntakeEntity
import com.app.pills.db.entity.ScheduleCardTimeSlotEntity

interface OnDeleteListener {

    fun onDelete(scheduleCardTimeSlotEntity: ScheduleCardTimeSlotEntity)

}