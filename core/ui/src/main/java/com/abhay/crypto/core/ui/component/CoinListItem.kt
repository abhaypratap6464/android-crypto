package com.abhay.crypto.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abhay.crypto.core.domain.model.Coin
import com.abhay.crypto.core.ui.theme.CardBackground
import com.abhay.crypto.core.ui.theme.Dimens
import com.abhay.crypto.core.ui.theme.IconInactive

@Composable
fun CoinListItem(
    coin: Coin,
    priceProvider: () -> String,
    isFoldered: Boolean,
    onAddToFolder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RadiusMedium),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        CoinListItemContent(
            coin = coin,
            priceProvider = priceProvider,
            isFoldered = isFoldered,
            onAddToFolder = onAddToFolder
        )
    }
}

@Composable
private fun CoinListItemContent(
    coin: Coin,
    priceProvider: () -> String,
    isFoldered: Boolean,
    onAddToFolder: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingExtraLarge, vertical = Dimens.PaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = coin.baseAsset,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = priceProvider(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )

        BookmarkButton(isFoldered, onAddToFolder)
    }
}

@Composable
private fun BookmarkButton(isFoldered: Boolean, onClick: () -> Unit) {
    val icon: ImageVector = if (isFoldered) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
    val contentDescription = if (isFoldered) "In a folder" else "Add to folder"
    val tint = if (isFoldered) MaterialTheme.colorScheme.primary else IconInactive

    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(Dimens.IconSmallMedium),
        )
    }
}
