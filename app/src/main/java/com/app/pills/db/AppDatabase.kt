package com.app.pills.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.pills.db.dao.PillDao
import com.app.pills.db.dao.PillIntakeDao
import com.app.pills.db.dao.ScheduleCardDao
import com.app.pills.db.dao.ScheduleCardPillDao
import com.app.pills.db.dao.ScheduleCardTimeSlotDao
import com.app.pills.db.entity.PillEntity
import com.app.pills.db.entity.PillIntakeEntity
import com.app.pills.db.entity.PillIntakePillEntity
import com.app.pills.db.entity.ScheduleCardEntity
import com.app.pills.db.entity.ScheduleCardPillEntity
import com.app.pills.db.entity.ScheduleCardTimeSlotEntity

@Database(
    entities = [
        PillEntity::class,
        ScheduleCardEntity::class,
        ScheduleCardPillEntity::class,
        ScheduleCardTimeSlotEntity::class,
        PillIntakeEntity::class,
        PillIntakePillEntity::class
    ], version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pillDao(): PillDao
    abstract fun scheduleCardDao(): ScheduleCardDao
    abstract fun scheduleCardPillDao(): ScheduleCardPillDao
    abstract fun scheduleCardTimeSlotDao(): ScheduleCardTimeSlotDao
    abstract fun pillIntakeDao(): PillIntakeDao
}