package com.mozzart.th.vostojin.domain

import com.mozzart.th.vostojin.FileCacheManager
import com.mozzart.th.vostojin.network.SportsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlin.collections.emptyList

private const val SPORTS_CACHE_FILENAME = "sports.json"
private const val COMPETITIONS_CACHE_FILENAME = "competitions.json"
private const val MATCHES_CACHE_FILENAME = "matches.json"

class SportsRepository(
    private val api: SportsApi, // Injected via Koin
    private val cache: FileCacheManager // Injected via KOin
) {

    // Logic for "Offline first"
    private fun <T> offlineFirstFlow(
        fileName: String,
        serializer: KSerializer<T>,
        networkCall: suspend () -> T
    ): Flow<T> = flow {

        // 1. Try to load from the file cache immediately
        val cachedData = cache.loadData(fileName, serializer)
        if (cachedData != null) {
            println("-- SportsRepository: Using cached data from '$fileName'")
            emit(cachedData)
        }

        // 2. Fetch from network
        try {
            val remoteData = networkCall()
            cache.saveData(fileName, serializer, remoteData)
            emit(remoteData)
        } catch (e: Exception) {
            println("-- SportsRepository: Network call failed for $fileName")
            // If we have cached data, all ok, already emitted.
            // If we don't have cached data, we emit an empty list
            if (cachedData == null) {
                println("-- SportsRepository: No cached data for '$fileName'")
                @Suppress("UNCHECKED_CAST")
                emit(emptyList<Any>() as T)
            }
        }
    }

    fun getSports(): Flow<List<Sport>> =
        offlineFirstFlow(SPORTS_CACHE_FILENAME, ListSerializer(Sport.serializer())) {
            api.fetchSports()
        }

    fun getCompetitions(): Flow<List<Competition>> =
        offlineFirstFlow(COMPETITIONS_CACHE_FILENAME, ListSerializer(Competition.serializer())) {
            api.fetchCompetitions()
        }

    fun getMatches(): Flow<List<Match>> =
        offlineFirstFlow(MATCHES_CACHE_FILENAME, ListSerializer(Match.serializer())) {
            api.fetchMatches()
        }
}