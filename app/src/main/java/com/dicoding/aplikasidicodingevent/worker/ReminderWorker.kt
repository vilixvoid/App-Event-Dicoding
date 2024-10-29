package com.dicoding.aplikasidicodingevent.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import com.dicoding.aplikasidicodingevent.di.Injection
import com.dicoding.aplikasidicodingevent.utils.NotificationUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import java.io.IOException
import java.lang.Exception

class ReminderWorker(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val repository: EventRepository = Injection.provideRepository(context)

    override suspend fun doWork(): Result {
        return try {
            val nearestActiveEvent = repository.getNearestActiveEvent().take(1).firstOrNull()
            nearestActiveEvent?.let {
                NotificationUtils.showReminderNotification(context, it)
            }
            Result.success()
        } catch (e: IOException) {
            Result.retry()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error showing reminder notification", e)
            Result.failure()
        }
    }
}
