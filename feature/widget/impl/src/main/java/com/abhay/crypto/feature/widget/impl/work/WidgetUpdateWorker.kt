package com.abhay.crypto.feature.widget.impl.work

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.abhay.crypto.core.domain.Constants
import com.abhay.crypto.feature.widget.impl.CryptoWidget
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Trigger a fresh render of all widgets.
        // The CryptoWidget.provideGlance() method will be called, which
        // will fetch fresh prices from REST (via getCoinsByIds).
        CryptoWidget().updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "WidgetUpdateWorker"

        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                Constants.WIDGET_UPDATE_INTERVAL_MINUTES, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
