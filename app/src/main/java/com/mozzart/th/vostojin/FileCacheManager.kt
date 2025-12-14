package com.mozzart.th.vostojin

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

// Replaces the file content with data from network.
// If a serialization/deserialization error occurs, the cache will not be updated.
class FileCacheManager(
    private val context: Context,
    private val json: Json // Injected via Koin
) {

    //private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true; coerceInputValues = true }

    // Generic function to save data to a file
    suspend fun <T> saveData(fileName: String, serializer: KSerializer<T>, data: T) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, fileName)
                val jsonString = json.encodeToString(serializer, data)
                file.writeText(jsonString)
            } catch (e: Exception) {
                ensureActive()
                println(("-- ERROR (FileCacheManager): ${e.message ?: "Error in saveData(...), cache was not updated."}"))
            }
        }
    }

    // Generic function to load data from a file
    suspend fun <T> loadData(fileName: String, serializer: KSerializer<T>): T? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, fileName)
                if (file.exists()) {
                    json.decodeFromString(serializer, file.readText())
                } else {
                    null
                }
            } catch (e: Exception) {
                ensureActive()
                println(("-- ERROR (FileCacheManager): ${e.message ?: "Error in saveData(...), cache was not used."}"))
                null
            }
        }
    }

}