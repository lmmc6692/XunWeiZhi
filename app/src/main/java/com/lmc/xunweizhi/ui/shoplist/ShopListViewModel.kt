package com.lmc.xunweizhi.ui.shoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lmc.xunweizhi.data.repository.ShopRepository
import com.lmc.xunweizhi.domain.Shop
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopListUiState(
    val query: String = "",
    val selectedTypeId: Long = 0,
    val shops: List<Shop> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val message: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class ShopListViewModel(
    private val repository: ShopRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedTypeId = MutableStateFlow(0L)
    private val screenState = MutableStateFlow(ShopListUiState())

    private val visibleShops = combine(
        selectedTypeId.flatMapLatest(repository::observeShops),
        query,
    ) { shops, keyword ->
        if (keyword.isBlank()) {
            shops
        } else {
            shops.filter { shop ->
                shop.name.contains(keyword, ignoreCase = true) ||
                    shop.area.contains(keyword, ignoreCase = true) ||
                    shop.tags.any { it.contains(keyword, ignoreCase = true) }
            }
        }
    }

    val uiState = combine(screenState, visibleShops) { state, shops ->
        state.copy(shops = shops, isLoading = state.isLoading && shops.isEmpty())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShopListUiState(),
    )

    init {
        viewModelScope.launch {
            repository.ensureDemoData()
            refresh()
        }
    }

    fun updateQuery(value: String) {
        query.value = value
        screenState.update { it.copy(query = value) }
    }

    fun selectType(typeId: Long) {
        if (selectedTypeId.value == typeId) return
        selectedTypeId.value = typeId
        screenState.update { it.copy(selectedTypeId = typeId, isLoading = true) }
        refresh()
    }

    fun refresh() {
        if (screenState.value.isRefreshing) return
        screenState.update { it.copy(isRefreshing = true, message = null) }
        viewModelScope.launch {
            val result = repository.refresh(selectedTypeId.value)
            screenState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    message = result.exceptionOrNull()?.let {
                        "暂时连接不到服务，正在展示本地精选内容"
                    },
                )
            }
        }
    }

    fun toggleFavorite(shopId: Long) {
        viewModelScope.launch { repository.toggleFavorite(shopId) }
    }

    fun clearMessage() {
        screenState.update { it.copy(message = null) }
    }

    companion object {
        fun factory(repository: ShopRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { ShopListViewModel(repository) }
        }
    }
}
