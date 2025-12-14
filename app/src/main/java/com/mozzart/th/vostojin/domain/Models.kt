package com.mozzart.th.vostojin.domain

import kotlinx.serialization.Serializable

@Serializable
data class Sport(
    val id: Int,
    val name: String,
    val sportIconUrl: String
)

@Serializable data class Competition(
    val id: Int,
    val sportId: Int,
    val name: String,
    val competitionIconUrl: String
)

@Serializable
data class Match(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamAvatar: String,
    val awayTeamAvatar: String,
    val date: String,
    val status: String,
    val currentTime: String? = null,
    val result: MatchResult? = null,
    val sportId: Int,
    val competitionId: Int
)

@Serializable
data class MatchResult(
    val home: Int,
    val away: Int
)
