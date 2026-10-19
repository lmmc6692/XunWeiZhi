package com.lmc.xunweizhi.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopApi {
    @GET("shop/of/type")
    suspend fun getShops(
        @Query("typeId") typeId: Long,
        @Query("current") current: Int = 1,
    ): ApiResponse<List<ShopDto>>

    @GET("shop/{id}")
    suspend fun getShop(@Path("id") id: Long): ApiResponse<ShopDto>
}

data class ApiResponse<T>(
    val success: Boolean,
    val errorMsg: String?,
    val data: T?,
    val total: Long?,
)

data class ShopDto(
    val id: Long,
    val name: String,
    val typeId: Long,
    val images: String?,
    val area: String?,
    val address: String?,
    val avgPrice: Long?,
    val sold: Int?,
    val comments: Int?,
    val score: Int?,
    val openHours: String?,
)
