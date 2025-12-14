package com.mozzart.th.vostojin.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.mozzart.th.vostojin.domain.Competition
import com.mozzart.th.vostojin.domain.Match
import com.mozzart.th.vostojin.domain.Sport
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAdjusters

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

            // Sports
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
            ) {

                // Sports
                if (state.sports.isNotEmpty()) {
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.sports) { sport ->
                                SportSelection(sport, state) {
                                    viewModel.setSportId(sport.id)
                                }
                            }
                        }
                    }
                }

                //// Competitions
                //item { SectionHeader("Competitions") }
                //if (state.competitions.isNotEmpty()) {
                //    items(state.competitions) { competition -> CompetitionRow(competition) }
                //} else {
                //    item { Text("Loading...") }
                //}

                // Live Matches
                item { SectionHeader("Mečevi uživo") }
                val liveMatches = state.matches.filter { it.status == "LIVE" }
                if (liveMatches.isNotEmpty()) {
                    items(liveMatches) { match ->
                        LiveMatchCard(
                            match,
                            state.competitions.firstOrNull { it.id == match.competitionId })
                    }
                } else {
                    item { Text("Nema mečeva...", modifier = Modifier.padding(16.dp)) }
                }

                // Prematch
                item {
                    Spacer(Modifier.height(16.dp))
                    SectionHeader("Prematch ponuda")
                }
                item {
                    LazyRow(
                        contentPadding = paddingValues,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        PrematchCategory.entries.forEach { prematchCategory ->
                            item {
                                PrematchCategorySelection(prematchCategory, state) {
                                    viewModel.setPrematchPeriod(prematchCategory)
                                }
                            }
                        }
                    }
                }
                val prematchMatches = state.matches.filter { it.status == "PRE_MATCH" && checkPrematchDate(it.date, state.selectedPrematchCategory) }
                if (prematchMatches.isNotEmpty()) {
                    items(prematchMatches) { match ->
                        PrematchMatchCard(
                            match,
                            state.competitions.firstOrNull { it.id == match.competitionId })
                    }
                } else {
                    item { Text("Nema mečeva...", modifier = Modifier.padding(16.dp)) }
                }
                //if (state.matches.isNotEmpty()) {
                //    items(state.matches.filter {
                //        it.status == "PRE_MATCH" && checkPrematchDate(it.date, state.selectedPrematchCategory)
                //    }) { match ->
                //        PrematchMatchCard(
                //            match,
                //            state.competitions.firstOrNull { it.id == match.competitionId })
                //    }
                //}
            }
        }
    }
}

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
fun parseDateTime(value: String): LocalDateTime {
    return try {
        LocalDateTime.parse(value, dateTimeFormatter)
    } catch (e: DateTimeParseException) {
        println("-- parseDateTime error: $value could not be parsed to LocalDateTime object, returning .now() instead... ${e.message}")
        LocalDateTime.now()
    }
}

private val today = LocalDateTime.now()
private val tomorrow = today.plusDays(1)
private val nextWeekendStart = today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
private val nextWeekStart = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
private val nextWeekEnd = nextWeekStart.plusDays(7)

fun checkPrematchDate(stringDate: String, category: PrematchCategory): Boolean {
    //return true

    val d: LocalDateTime = parseDateTime(stringDate)

    return when (category) {
        PrematchCategory.TODAY -> d == today
        PrematchCategory.TOMORROW -> d == tomorrow
        PrematchCategory.WEEKEND -> d in nextWeekendStart..<nextWeekStart
        PrematchCategory.NEXT_WEEK -> d in nextWeekStart..<nextWeekEnd
        PrematchCategory.LATER -> d.isAfter(nextWeekEnd)
    }
}

@Composable
fun SectionHeader(title: String) {
    Spacer(Modifier.height(24.dp))
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .drawBehind {
                val strokeWidth = 3.dp.toPx()
                val xyOffset = 6.dp.toPx()
                drawLine(
                    color = Color.Red,
                    start = Offset(xyOffset, xyOffset),
                    end = Offset(xyOffset, size.height - xyOffset),
                    strokeWidth = strokeWidth
                )
            }
            .padding(horizontal = 16.dp)
    )
}

@Composable
fun SportSelection(
    sport: Sport,
    state: DashboardState,
    onClick: () -> Unit
) {
    val isSelected = sport.id == state.selectedSportId
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.Yellow else CardDefaults.cardColors().containerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(sport.sportIconUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .crossfade(true)
                    .build(),
                contentDescription = "Icon for ${sport.name}",
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = sport.name, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun MatchTeamAvatar(avatarUrl: String, teamName: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(avatarUrl)
            .decoderFactory(SvgDecoder.Factory())
            .crossfade(true)
            .build(),
        contentDescription = "Icon for $teamName",
        modifier = Modifier.size(32.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun LiveMatchCard(match: Match, competition: Competition?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                competition?.let {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.competitionIconUrl)
                            .decoderFactory(SvgDecoder.Factory())
                            .crossfade(true)
                            .build(),
                        contentDescription = "Icon for ${it.name}",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = it.name, style = MaterialTheme.typography.bodySmall)
                }
                match.currentTime?.let {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(match.currentTime, color = Color(0xFF006415))
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                MatchTeamAvatar(match.homeTeamAvatar, match.homeTeam)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = match.homeTeam, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Start)
                if (match.result != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${match.result.home}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                MatchTeamAvatar(match.awayTeamAvatar, match.awayTeam)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = match.awayTeam, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Start)
                if (match.result != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${match.result.away}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
fun PrematchCategorySelection(prematchCategory: PrematchCategory, state: DashboardState, onClick: () -> Unit) {
    val isSelected = prematchCategory == state.selectedPrematchCategory
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.Yellow else CardDefaults.cardColors().containerColor)
    ) {
        Text(
            text = prematchCategory.label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }

}


@Composable
fun PrematchTeamWithAvatar(modifier: Modifier, avatarUrl: String, teamName: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(Modifier.height(8.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .decoderFactory(SvgDecoder.Factory())
                .crossfade(true)
                .build(),
            contentDescription = "Icon for $teamName",
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(4.dp))
        Text(text = teamName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
    }
}

private val shortDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val shortTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun PrematchLeagueAndDate(modifier: Modifier, competition: Competition?, dateTime: LocalDateTime) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(competition?.competitionIconUrl)
                .decoderFactory(SvgDecoder.Factory())
                .crossfade(true)
                .build(),
            contentDescription = "Icon for ${competition?.name}",
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit,

            )
        Spacer(Modifier.height(4.dp))
        Text(
            text = competition?.name ?: "Unknown",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(Modifier.height(12.dp))
        Text(text = dateTime.format(shortDateFormatter), style = MaterialTheme.typography.bodyLarge)
        Text(
            text = dateTime.format(shortTimeFormatter),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PrematchMatchCard(match: Match, competition: Competition?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val m = Modifier.padding(8.dp).weight(1f)
            PrematchTeamWithAvatar(m, match.homeTeamAvatar, match.homeTeam)
            PrematchLeagueAndDate(m, competition = competition, dateTime = parseDateTime(match.date))
            PrematchTeamWithAvatar(m, match.awayTeamAvatar, match.awayTeam)
        }

    }
}