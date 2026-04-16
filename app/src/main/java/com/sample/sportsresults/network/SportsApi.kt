package com.sample.sportsresults.network

import com.sample.sportsresults.domain.Competition
import com.sample.sportsresults.domain.Match
import com.sample.sportsresults.domain.Sport
import retrofit2.http.GET

interface SportsApi {

    @GET("sports")
    suspend fun fetchSports(): List<Sport>

    @GET("competitions")
    suspend fun fetchCompetitions(): List<Competition>

    @GET("matches")
    suspend fun fetchMatches(): List<Match>

}