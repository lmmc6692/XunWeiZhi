package com.lmc.xunweizhi.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lmc.xunweizhi.data.repository.ShopRepository
import com.lmc.xunweizhi.domain.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopDetailUiState(
    val shop: Shop? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val message: String? = null,
)

class ShopDetailViewModel(
    private val shopId: Long,
    private val repository: ShopRepository,
) : ViewModel() {
    private val screenState = MutableStateFlow(ShopDetailUiState())

    val uiState = combine(screenState, repository.observeShop(shopId)) { state, shop ->
        state.copy(
            shop = shop,
            isLoading = state.isLoading && shop == null,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShopDetailUiState(),
    )

    init {
        viewModelScope.launch {
            repository.ensureDemoData()
            refresh()
        }
    }

    fun refresh() {
        if (screenState.value.isRefreshing) return
        screenState.update { it.copy(isRefreshing = true, message = null) }
        viewModelScope.launch {
            val result = repository.refreshDetail(shopId)
            screenState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    message = result.exceptionOrNull()?.let {
                        "网络不可用，当前展示已缓存的信息"
                    },
                )
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch { repository.toggleFavorite(shopId) }
    }

    companion object {
        fun factory(
            shopId: Long,
            repository: ShopRepository,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ShopDetailViewModel(shopId, repository) }
        }
    }
}
