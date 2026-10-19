package com.lmc.xunweizhi.ui.shoplist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lmc.xunweizhi.domain.Shop
import com.lmc.xunweizhi.domain.ShopCategories
import com.lmc.xunweizhi.domain.ShopCategory
import com.lmc.xunweizhi.ui.components.OfflineBanner
import com.lmc.xunweizhi.ui.components.ShopListCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopListScreen(
    viewModel: ShopListViewModel,
    onShopClick: (Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "寻味志",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "今天，想吃点什么？",
                    style = MaterialTheme.typography.displaySmall,
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    text = "杭州",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("搜店名、商圈或想吃的菜") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                if (state.query.isNotBlank()) {
                    IconButton(onClick = { viewModel.updateQuery("") }) {
                        Icon(Icons.Outlined.Close, contentDescription = "清空搜索")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp),
        ) {
            items(ShopCategories, key = ShopCategory::id) { category ->
                CategoryChip(
                    category = category,
                    selected = state.selectedTypeId == category.id,
                    onClick = { viewModel.selectType(category.id) },
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        if (state.message != null) {
            OfflineBanner(
                message = state.message.orEmpty(),
                onRetry = viewModel::refresh,
            )
            Spacer(Modifier.height(12.dp))
        }
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.weight(1f),
        ) {
            when {
                state.isLoading -> LoadingContent()
                state.shops.isEmpty() -> EmptyContent(
                    hasQuery = state.query.isNotBlank(),
                    onClear = {
                        viewModel.updateQuery("")
                        viewModel.selectType(0)
                    },
                )

                else -> ShopList(
                    shops = state.shops,
                    onShopClick = onShopClick,
                    onFavoriteClick = viewModel::toggleFavorite,
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: ShopCategory,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val icons = mapOf(
        0L to Icons.Outlined.Restaurant,
        1L to Icons.Outlined.Restaurant,
        2L to Icons.Outlined.SportsEsports,
        3L to Icons.Outlined.ContentCut,
        4L to Icons.Outlined.FitnessCenter,
    )
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(category.label) },
        leadingIcon = {
            Icon(
                imageVector = icons[category.id] ?: Icons.Outlined.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun ShopList(
    shops: List<Shop>,
    onShopClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "附近精选",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${shops.size} 家",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(shops, key = Shop::id) { shop ->
            ShopListCard(
                shop = shop,
                onClick = { onShopClick(shop.id) },
                onFavoriteClick = { onFavoriteClick(shop.id) },
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            strokeWidth = 2.dp,
        )
    }
}

@Composable
private fun EmptyContent(
    hasQuery: Boolean,
    onClear: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(42.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (hasQuery) "没有找到相关店铺" else "这个分类还没有内容",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (hasQuery) "换个关键词或看看其他分类" else "稍后再来看看",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (hasQuery) {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onClear) {
                    Text("查看全部", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
