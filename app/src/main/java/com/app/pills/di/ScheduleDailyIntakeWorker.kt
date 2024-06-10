package com.app.pills.di

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.app.pills.db.dao.PillIntakeDao
import com.app.pills.model.Util
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@HiltWorker
class ScheduleDailyIntakeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workParams: WorkerParameters,
    private val pillIntakeDao: PillIntakeDao
) : CoroutineWorker(context, workParams) {

    private val util = Util()

    override suspend fun doWork(): Result {
        val pillIntakes = pillIntakeDao.getAllPillIntakes().value?.filter {
            util.longToLocalDateTime(it.time).toLocalDate() == LocalDate.now()
        }
        pillIntakes?.forEach { pillIntake ->
            val delay = pillIntake.time - System.currentTimeMillis()
            if (delay > 0 && pillIntake.isDone == 0) {
                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()
                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
        return Result.success()
    }
}