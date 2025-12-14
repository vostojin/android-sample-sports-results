package com.mozzart.th.vostojin.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mozzart.th.vostojin.domain.Competition
import com.mozzart.th.vostojin.domain.Match
import com.mozzart.th.vostojin.domain.Sport

@Composable
fun DashboardScreen(
    viewModel: SportsViewModel
) {

    val state by viewModel.uiState.collectAsState()

    Scaffold() { paddingValues ->

        if (state.isLoading && state.sports.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {

            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
            ) {

                // Sports
                item { SectionHeader("Sports") }
                if (state.sports.isNotEmpty()) {
                    items(state.sports) { sport -> SportRow(sport) }
                } else {
                    item { Text("Loading...") }
                }

                // Competitions
                item { SectionHeader("Competitions") }
                if (state.competitions.isNotEmpty()) {
                    items(state.competitions) { competition -> CompetitionRow(competition) }
                } else {
                    item { Text("Loading...") }
                }

                // Sports
                item { SectionHeader("Matches") }
                if (state.matches.isNotEmpty()) {
                    items(state.matches) { match -> MatchCard(match) }
                } else {
                    item { Text("Loading...") }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun SportRow(sport: Sport) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            // In a real app, use Coil/Glide for sport.sportIconUrl
            Text(text = sport.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun CompetitionRow(competition: Competition) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            // In a real app, use Coil/Glide for sport.sportIconUrl
            Text(text = competition.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun MatchCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (match.status == "LIVE") Color(0xFFFFEBEE) else Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = match.homeTeam, style = MaterialTheme.typography.bodyLarge)
                Text(text = "vs", style = MaterialTheme.typography.labelSmall)
                Text(text = match.awayTeam, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (match.status == "LIVE" && match.result != null) {
                Text(
                    text = "${match.result.home} - ${match.result.away}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Red
                )
            } else {
                Text(
                    text = match.date,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}