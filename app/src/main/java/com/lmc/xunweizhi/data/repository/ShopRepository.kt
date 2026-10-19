package com.lmc.xunweizhi.data.repository

import com.lmc.xunweizhi.data.local.ShopDao
import com.lmc.xunweizhi.data.local.ShopEntity
import com.lmc.xunweizhi.data.local.toDomain
import com.lmc.xunweizhi.data.remote.ShopApi
import com.lmc.xunweizhi.data.remote.ShopDto
import com.lmc.xunweizhi.domain.Shop
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShopRepository(
    private val dao: ShopDao,
    private val api: ShopApi,
) {
    fun observeShops(typeId: Long): Flow<List<Shop>> {
        val source = if (typeId == 0L) dao.observeAll() else dao.observeByType(typeId)
        return source.map { shops -> shops.map(ShopEntity::toDomain) }
    }

    fun observeFavorites(): Flow<List<Shop>> =
        dao.observeFavorites().map { shops -> shops.map(ShopEntity::toDomain) }

    fun observeShop(id: Long): Flow<Shop?> =
        dao.observeById(id).map { it?.toDomain() }

    suspend fun ensureDemoData() {
        if (dao.count() == 0) {
            dao.upsertAll(DemoShops.shops)
        }
    }

    suspend fun refresh(typeId: Long): Result<Unit> = runCatching {
        val ids = if (typeId == 0L) listOf(1L, 2L, 3L, 4L) else listOf(typeId)
        val favoriteIds = dao.favoriteIds().toSet()
        val responses = coroutineScope {
            ids.map { id ->
                async {
                    runCatching {
                        val response = api.getShops(typeId = id)
                        if (!response.success) {
                            error(response.errorMsg ?: "店铺列表加载失败")
                        }
                        response.data.orEmpty()
                    }
                }
            }.awaitAll()
        }
        val remoteShops = responses.flatMap { it.getOrElse { emptyList() } }
        if (remoteShops.isEmpty()) {
            responses.firstNotNullOfOrNull { it.exceptionOrNull() }?.let { throw it }
        } else {
            dao.deleteDemoData()
            dao.upsertAll(remoteShops.map { it.toEntity(favoriteIds.contains(it.id)) })
        }
    }

    suspend fun refreshDetail(id: Long): Result<Unit> = runCatching {
        val response = api.getShop(id)
        if (!response.success || response.data == null) {
            error(response.errorMsg ?: "店铺详情加载失败")
        }
        val favorite = dao.findById(id)?.isFavorite ?: false
        dao.upsertAll(listOf(response.data.toEntity(favorite)))
    }

    suspend fun toggleFavorite(id: Long) {
        val shop = dao.findById(id) ?: return
        val nextFavorite = !shop.isFavorite
        dao.setFavorite(
            id = id,
            favorite = nextFavorite,
            favoriteAt = if (nextFavorite) System.currentTimeMillis() else null
        )
    }

    private fun ShopDto.toEntity(isFavorite: Boolean) = ShopEntity(
        id = id,
        name = name,
        typeId = typeId,
        imageUrl = images.orEmpty().split(",").firstOrNull().orEmpty(),
        area = area.orEmpty().ifBlank { "附近" },
        address = address.orEmpty(),
        averagePrice = avgPrice?.toInt() ?: 0,
        sold = sold ?: 0,
        comments = comments ?: 0,
        score = (score ?: 0) / 10.0,
        openHours = openHours.orEmpty(),
        summary = "来自寻味志用户的本地生活精选店铺。",
        tags = listOf("本地精选", "人气店铺").joinToString("|"),
        isFavorite = isFavorite,
        favoriteAt = if (isFavorite) System.currentTimeMillis() else null,
    )
}
