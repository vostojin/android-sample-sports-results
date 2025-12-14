package com.mozzart.th.vostojin.network

import com.mozzart.th.vostojin.domain.Competition
import com.mozzart.th.vostojin.domain.Match
import com.mozzart.th.vostojin.domain.Sport
import retrofit2.http.GET

interface SportsApi {

    @GET("sports")
    suspend fun fetchSports(): List<Sport>

    @GET("competitions")
    suspend fun fetchCompetitions(): List<Competition>

    @GET("matches")
    suspend fun fetchMatches(): List<Match>

}