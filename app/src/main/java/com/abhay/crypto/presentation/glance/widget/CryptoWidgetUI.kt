package com.abhay.crypto.presentation.glance.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.abhay.crypto.MainActivity
import com.abhay.crypto.R
import com.abhay.crypto.domain.model.Coin

private const val MAX_WIDGET_COINS = 5

@Composable
fun CryptoWidgetContent(
    folderName: String,
    coins: List<Coin>,
    isOnline: Boolean,
    formatPrice: (Double) -> String
) {
    val context = LocalContext.current
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .cornerRadius(16.dp)
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = folderName,
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            Image(
                provider = ImageProvider(R.drawable.ic_refresh),
                contentDescription = context.getString(R.string.refresh_desc),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                modifier = GlanceModifier
                    .clickable(actionRunCallback<RefreshActionCallback>())
                    .padding(start = 8.dp, bottom = 8.dp)
            )
        }

        if (!isOnline) {
            Text(
                text = context.getString(R.string.offline_status),
                style = TextStyle(
                    color = GlanceTheme.colors.error,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = 8.dp)
            )
        } else {
            if (coins.isEmpty()) {
                Text(
                    text = context.getString(R.string.no_bookmarks_yet),
                    style = TextStyle(color = GlanceTheme.colors.onBackground)
                )
            } else {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    coins.take(MAX_WIDGET_COINS).forEach { coin ->
                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = coin.baseAsset,
                                style = TextStyle(color = GlanceTheme.colors.onBackground)
                            )

                            Spacer(modifier = GlanceModifier.defaultWeight())

                            Text(
                                text = formatPrice(coin.price),
                                style = TextStyle(
                                    color = GlanceTheme.colors.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
