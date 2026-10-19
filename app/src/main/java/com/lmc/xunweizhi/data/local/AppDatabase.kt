package com.lmc.xunweizhi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ShopEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
}
