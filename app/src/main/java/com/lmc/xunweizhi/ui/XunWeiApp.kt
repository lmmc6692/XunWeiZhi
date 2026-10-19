package com.lmc.xunweizhi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lmc.xunweizhi.XunWeiApplication
import com.lmc.xunweizhi.data.repository.ShopRepository
import com.lmc.xunweizhi.ui.detail.ShopDetailScreen
import com.lmc.xunweizhi.ui.detail.ShopDetailViewModel
import com.lmc.xunweizhi.ui.favorites.FavoritesScreen
import com.lmc.xunweizhi.ui.favorites.FavoritesViewModel
import com.lmc.xunweizhi.ui.shoplist.ShopListScreen
import com.lmc.xunweizhi.ui.shoplist.ShopListViewModel

private object Routes {
    const val Home = "home"
    const val Favorites = "favorites"
    const val Detail = "shop/{shopId}"

    fun detail(shopId: Long) = "shop/$shopId"
}

@Composable
fun XunWeiApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val repository = rememberRepository()
    val showBottomBar = currentRoute == Routes.Home || currentRoute == Routes.Favorites

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onHome = {
                        navController.navigate(Routes.Home) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onFavorites = {
                        navController.navigate(Routes.Favorites) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(Routes.Home) {
                val listViewModel: ShopListViewModel = viewModel(
                    factory = ShopListViewModel.factory(repository)
                )
                ShopListScreen(
                    viewModel = listViewModel,
                    onShopClick = { navController.navigate(Routes.detail(it)) },
                )
            }
            composable(Routes.Favorites) {
                val favoritesViewModel: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModel.factory(repository)
                )
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onShopClick = { navController.navigate(Routes.detail(it)) },
                    onExplore = {
                        navController.navigate(Routes.Home) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(
                route = Routes.Detail,
                arguments = listOf(navArgument("shopId") { type = NavType.LongType }),
            ) { entry ->
                val shopId = entry.arguments?.getLong("shopId") ?: 0L
                val detailViewModel: ShopDetailViewModel = viewModel(
                    factory = ShopDetailViewModel.factory(shopId, repository)
                )
                ShopDetailScreen(
                    viewModel = detailViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun rememberRepository(): ShopRepository {
    val context = LocalContext.current
    return (context.applicationContext as XunWeiApplication).container.shopRepository
}

@Composable
private fun AppBottomBar(
    currentRoute: String?,
    onHome: () -> Unit,
    onFavorites: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            BottomItem(
                label = "发现",
                icon = Icons.Outlined.Explore,
                selected = currentRoute == Routes.Home,
                onClick = onHome,
                modifier = Modifier.weight(1f),
            )
            BottomItem(
                label = "收藏",
                icon = Icons.Outlined.FavoriteBorder,
                selected = currentRoute == Routes.Favorites,
                onClick = onFavorites,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 5.dp),
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}
