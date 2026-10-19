package com.lmc.xunweizhi.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops ORDER BY typeId ASC, score DESC, sold DESC")
    fun observeAll(): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shops WHERE typeId = :typeId ORDER BY score DESC, sold DESC")
    fun observeByType(typeId: Long): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shops WHERE isFavorite = 1 ORDER BY favoriteAt DESC")
    fun observeFavorites(): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shops WHERE id = :id")
    fun observeById(id: Long): Flow<ShopEntity?>

    @Query("SELECT * FROM shops WHERE id = :id")
    suspend fun findById(id: Long): ShopEntity?

    @Query("SELECT id FROM shops WHERE isFavorite = 1")
    suspend fun favoriteIds(): List<Long>

    @Query("SELECT COUNT(*) FROM shops")
    suspend fun count(): Int

    @Upsert
    suspend fun upsertAll(shops: List<ShopEntity>)

    @Query("DELETE FROM shops WHERE id < 0")
    suspend fun deleteDemoData()

    @Query("UPDATE shops SET isFavorite = :favorite, favoriteAt = :favoriteAt WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean, favoriteAt: Long?)
}
