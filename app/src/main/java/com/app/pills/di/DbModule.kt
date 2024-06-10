package com.app.pills.di

import android.content.Context
import androidx.room.Room
import com.app.pills.db.AppDatabase
import com.app.pills.db.dao.PillDao
import com.app.pills.db.dao.PillIntakeDao
import com.app.pills.db.dao.ScheduleCardDao
import com.app.pills.db.dao.ScheduleCardPillDao
import com.app.pills.db.dao.ScheduleCardTimeSlotDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app-database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providePillDao(appDatabase: AppDatabase): PillDao {
        return appDatabase.pillDao()
    }

    @Provides
    fun providePillIntakeDao(appDatabase: AppDatabase): PillIntakeDao {
        return appDatabase.pillIntakeDao()
    }

    @Provides
    fun provideScheduleCardDao(appDatabase: AppDatabase): ScheduleCardDao {
        return appDatabase.scheduleCardDao()
    }

    @Provides
    fun provideScheduleCardPillDao(appDatabase: AppDatabase): ScheduleCardPillDao {
        return appDatabase.scheduleCardPillDao()
    }

    @Provides
    fun provideScheduleCardTimeSlotDao(appDatabase: AppDatabase): ScheduleCardTimeSlotDao {
        return appDatabase.scheduleCardTimeSlotDao()
    }

}