package com.abhay.crypto.feature.widget.impl

import android.content.ComponentName
import androidx.compose.runtime.Composable
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
import com.abhay.crypto.core.domain.Constants
import com.abhay.crypto.core.domain.model.Coin
import com.abhay.crypto.core.ui.R
import com.abhay.crypto.core.ui.theme.Dimens

@Composable
fun CryptoWidgetContent(
    folderName: String,
    coins: List<Coin>,
    isOnline: Boolean,
    formatPrice: (Double) -> String
) {
    val context = LocalContext.current
    val componentName = ComponentName(context.packageName, "com.abhay.crypto.MainActivity")

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .cornerRadius(Dimens.RadiusLarge)
            .padding(Dimens.PaddingExtraLarge)
            .clickable(actionStartActivity(componentName))
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
                modifier = GlanceModifier.padding(bottom = Dimens.SpacingMedium)
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            Image(
                provider = ImageProvider(R.drawable.ic_refresh),
                contentDescription = context.getString(R.string.refresh_desc),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                modifier = GlanceModifier
                    .clickable(actionRunCallback<RefreshActionCallback>())
                    .padding(start = Dimens.SpacingMedium, bottom = Dimens.SpacingMedium)
            )
        }

        if (!isOnline) {
            Text(
                text = context.getString(R.string.offline_status),
                style = TextStyle(
                    color = GlanceTheme.colors.error,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = Dimens.SpacingMedium)
            )
        } else {
            if (coins.isEmpty()) {
                Text(
                    text = context.getString(R.string.no_bookmarks_yet),
                    style = TextStyle(color = GlanceTheme.colors.onBackground)
                )
            } else {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    coins.take(Constants.MAX_WIDGET_COINS).forEach { coin ->
                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.SpacingSmall),
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
