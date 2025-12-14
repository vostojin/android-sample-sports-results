package com.mozzart.th.vostojin.di

import com.mozzart.th.vostojin.FileCacheManager
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.get
import org.koin.dsl.module

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
}