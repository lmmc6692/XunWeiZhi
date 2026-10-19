package com.lmc.xunweizhi.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lmc.xunweizhi.data.repository.ShopRepository
import com.lmc.xunweizhi.domain.Shop
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: ShopRepository,
) : ViewModel() {
    val favorites = repository.observeFavorites().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<Shop>(),
    )

    fun toggleFavorite(shopId: Long) {
        viewModelScope.launch { repository.toggleFavorite(shopId) }
    }

    companion object {
        fun factory(repository: ShopRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { FavoritesViewModel(repository) }
        }
    }
}
