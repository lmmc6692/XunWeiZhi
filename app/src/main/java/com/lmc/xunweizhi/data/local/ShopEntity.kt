package com.lmc.xunweizhi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lmc.xunweizhi.domain.Shop

@Entity(tableName = "shops")
data class ShopEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val typeId: Long,
    val imageUrl: String,
    val area: String,
    val address: String,
    val averagePrice: Int,
    val sold: Int,
    val comments: Int,
    val score: Double,
    val openHours: String,
    val summary: String,
    val tags: String,
    val isFavorite: Boolean,
    val favoriteAt: Long?,
)

fun ShopEntity.toDomain() = Shop(
    id = id,
    name = name,
    typeId = typeId,
    imageUrl = imageUrl,
    area = area,
    address = address,
    averagePrice = averagePrice,
    sold = sold,
    comments = comments,
    score = score,
    openHours = openHours,
    summary = summary,
    tags = tags.split("|").filter(String::isNotBlank),
    isFavorite = isFavorite,
)
