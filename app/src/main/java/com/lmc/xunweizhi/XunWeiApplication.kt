package com.lmc.xunweizhi

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.lmc.xunweizhi.data.local.AppDatabase
import com.lmc.xunweizhi.data.remote.ShopApi
import com.lmc.xunweizhi.data.repository.ShopRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class XunWeiApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "xunweizhi.db"
    ).fallbackToDestructiveMigration(dropAllTables = true).build()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ShopApi::class.java)

    val shopRepository = ShopRepository(database.shopDao(), api)
}
