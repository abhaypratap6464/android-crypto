package com.abhay.crypto.presentation.glance.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.abhay.crypto.domain.model.BookmarkFolder
import com.abhay.crypto.domain.repository.FolderRepository
import com.abhay.crypto.presentation.theme.CryptoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetConfigurationActivity : ComponentActivity() {

    @Inject
    lateinit var folderRepository: FolderRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // Default to cancelled so backing out doesn't leave a broken widget.
        setResult(RESULT_CANCELED)

        setContent {
            CryptoTheme {
                val folders by folderRepository.getFolders()
                    .collectAsStateWithLifecycle(initialValue = emptyList())

                Scaffold(
                    topBar = { TopAppBar(title = { Text("Choose a folder") }) }
                ) { padding ->
                    if (folders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("No folders yet — create one in the app first.")
                        }
                    } else {
                        LazyColumn(contentPadding = padding) {
                            items(folders, key = BookmarkFolder::id) { folder ->
                                ListItem(
                                    headlineContent = { Text(folder.name) },
                                    supportingContent = {
                                        Text("${folder.coinIds.size} coin${if (folder.coinIds.size == 1) "" else "s"}")
                                    },
                                    modifier = Modifier.clickable {
                                        lifecycleScope.launch {
                                            saveAndFinish(appWidgetId, folder.id)
                                        }
                                    },
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun saveAndFinish(appWidgetId: Int, folderId: String) {
        val glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        updateAppWidgetState(this, glanceId) { prefs ->
            prefs[CryptoWidget.FOLDER_ID_KEY] = folderId
        }
        CryptoWidget().update(this, glanceId)
        setResult(
            RESULT_OK,
            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
        )
        finish()
    }
}
