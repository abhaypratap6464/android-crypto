package com.abhay.crypto.feature.widget.impl

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.abhay.crypto.core.domain.model.BookmarkFolder
import com.abhay.crypto.core.domain.repository.FolderRepository
import com.abhay.crypto.core.ui.R
import com.abhay.crypto.core.ui.theme.CryptoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetConfigurationActivity : ComponentActivity() {

    @Inject
    lateinit var folderRepository: FolderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appWidgetId = getAppWidgetId() ?: return

        // Default to cancelled so backing out doesn't leave a broken widget.
        setResult(RESULT_CANCELED)

        setContent {
            CryptoTheme {
                WidgetConfigurationScreen(
                    folderRepository = folderRepository,
                    onFolderSelected = { folderId ->
                        lifecycleScope.launch {
                            saveAndFinish(appWidgetId, folderId)
                        }
                    }
                )
            }
        }
    }

    private fun getAppWidgetId(): Int? {
        val appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        return if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            null
        } else {
            appWidgetId
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetConfigurationScreen(
    folderRepository: FolderRepository,
    onFolderSelected: (String) -> Unit
) {
    val folders by folderRepository.getFolders()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.choose_a_folder)) }) }
    ) { padding ->
        if (folders.isEmpty()) {
            EmptyFoldersMessage(padding)
        } else {
            FolderList(folders, padding, onFolderSelected)
        }
    }
}

@Composable
private fun EmptyFoldersMessage(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.no_folders_yet_create_one_in_the_app_first))
    }
}

@Composable
private fun FolderList(
    folders: List<BookmarkFolder>,
    padding: PaddingValues,
    onFolderSelected: (String) -> Unit
) {
    LazyColumn(contentPadding = padding) {
        items(folders, key = BookmarkFolder::id) { folder ->
            ListItem(
                headlineContent = { Text(folder.name) },
                supportingContent = {
                    val count = folder.coinIds.size
                    Text("$count coin${if (count == 1) "" else "s"}")
                },
                modifier = Modifier.clickable { onFolderSelected(folder.id) },
            )
            HorizontalDivider()
        }
    }
}
