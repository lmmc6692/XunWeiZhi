package com.lmc.xunweizhi.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lmc.xunweizhi.domain.Shop
import com.lmc.xunweizhi.ui.components.OfflineBanner
import com.lmc.xunweizhi.ui.components.RatingPill
import com.lmc.xunweizhi.ui.components.ShopImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
    viewModel: ShopDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                )
            }
        }

        state.shop == null -> {
            MissingShop(onBack = onBack)
        }

        else -> {
            val shop = state.shop ?: return
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 28.dp),
                ) {
                    item {
                        HeroImage(
                            shop = shop,
                            isFavorite = shop.isFavorite,
                            onBack = onBack,
                            onFavorite = viewModel::toggleFavorite,
                        )
                    }
                    item {
                        DetailBody(
                            shop = shop,
                            message = state.message,
                            onRetry = viewModel::refresh,
                            onFavorite = viewModel::toggleFavorite,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroImage(
    shop: Shop,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onFavorite: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(304.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        ShopImage(
            shop = shop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.28f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.18f),
                        )
                    )
                )
                .then(Modifier),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OverlayIconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
            }
            OverlayIconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite) "取消收藏" else "收藏",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun OverlayIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        shadowElevation = 2.dp,
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(42.dp),
            content = content,
        )
    }
}

@Composable
private fun DetailBody(
    shop: Shop,
    message: String?,
    onRetry: () -> Unit,
    onFavorite: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        if (message != null) {
            OfflineBanner(message = message, onRetry = onRetry)
            Spacer(Modifier.height(14.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = shop.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            RatingPill(score = shop.score)
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shop.tags) { tag ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = shop.summary,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(18.dp))
        InfoRow(
            icon = Icons.Outlined.LocationOn,
            title = "${shop.area} · ${shop.address}",
        )
        Spacer(Modifier.height(13.dp))
        InfoRow(
            icon = Icons.Outlined.Schedule,
            title = shop.openHours.ifBlank { "营业时间请电话咨询" },
        )
        Spacer(Modifier.height(13.dp))
        InfoRow(
            icon = Icons.Outlined.RestaurantMenu,
            title = "人均 ¥${shop.averagePrice} · 已售 ${shop.sold} · ${shop.comments} 条评价",
        )
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onFavorite,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (shop.isFavorite) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.primary
                }
            ),
        ) {
            Icon(
                imageVector = if (shop.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (shop.isFavorite) "已收藏" else "收藏这家店",
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(19.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MissingShop(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "没有找到这家店",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "返回列表看看其他推荐",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = onBack) {
                Text("返回")
            }
        }
    }
}
