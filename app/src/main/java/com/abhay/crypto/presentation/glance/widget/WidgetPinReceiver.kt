package com.abhay.crypto.presentation.glance.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receives the success callback from [AppWidgetManager.requestPinAppWidget].
 * At this point Android has created the widget instance and given us its ID,
 * so we can immediately save the folder the user pinned and trigger a render.
 */
class WidgetPinReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val folderId = intent.getStringExtra(EXTRA_FOLDER_ID) ?: return
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        )
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

        // goAsync() keeps the receiver alive long enough for the coroutine to finish.
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[CryptoWidget.FOLDER_ID_KEY] = folderId
                }
                CryptoWidget().update(context, glanceId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_FOLDER_ID = "extra_folder_id"
    }
}
