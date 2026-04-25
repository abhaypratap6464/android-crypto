package com.abhay.crypto.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abhay.crypto.core.ui.R
import com.abhay.crypto.core.ui.theme.Dimens
import com.abhay.crypto.core.ui.theme.TextSecondary

@Composable
fun ErrorView(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNetworkError = error is java.net.UnknownHostException
            || error is java.net.ConnectException

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(Dimens.PaddingExtraLarge),
        ) {
            if (isNetworkError) {
                Icon(
                    imageVector = Icons.Default.SignalWifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(Dimens.IconLarge)
                        .padding(bottom = Dimens.PaddingExtraLarge),
                )
                Text(
                    text = stringResource(R.string.no_internet),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
                Text(
                    text = stringResource(R.string.network_error_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            } else {
                Text(
                    text = error.message ?: stringResource(R.string.failed_to_load_coins),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.try_again))
            }
        }
    }
}
