package com.lmc.xunweizhi.domain

data class Shop(
    val id: Long,
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
    val tags: List<String>,
    val isFavorite: Boolean,
)

data class ShopCategory(
    val id: Long,
    val label: String,
)

val ShopCategories = listOf(
    ShopCategory(0, "全部"),
    ShopCategory(1, "美食"),
    ShopCategory(2, "玩乐"),
    ShopCategory(3, "丽人"),
    ShopCategory(4, "运动"),
)
