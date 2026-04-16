package com.sample.sportsresults.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sample.sportsresults.FileCacheManager
import com.sample.sportsresults.domain.SportsRepository
import com.sample.sportsresults.network.SportsApi
import com.sample.sportsresults.presentation.SportsViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val appModule = module {

    // JSON configuration
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            coerceInputValues = true
        }
    }

    // FileCacheManager
    single {
        FileCacheManager(androidContext(), get())
    }

    // Network Client (OkHttp)
    single {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit
    single {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("https://take-home-api-7m87.onrender.com/api/")
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory(contentType))
            .build()
            .create(SportsApi::class.java)
    }

    // Repository
    single {
        SportsRepository(get(), get())
    }

    // ViewModel
    viewModel {
        SportsViewModel(get())
    }
}