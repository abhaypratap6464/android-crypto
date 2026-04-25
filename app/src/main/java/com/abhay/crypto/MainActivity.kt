package com.abhay.crypto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.abhay.crypto.core.ui.theme.CryptoTheme
import com.abhay.crypto.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

typealias NavEntryBuilder = @JvmSuppressWildcards (EntryProviderScope<NavKey>.() -> Unit)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var entryBuilders: Set<NavEntryBuilder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTheme {
                NavGraph(entryBuilders = entryBuilders)
            }
        }
    }
}
