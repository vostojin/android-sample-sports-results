package com.mozzart.th.vostojin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mozzart.th.vostojin.domain.Competition
import com.mozzart.th.vostojin.domain.Match
import com.mozzart.th.vostojin.domain.Sport
import com.mozzart.th.vostojin.domain.SportsRepository
import kotlinx.coroutines.flow.*

data class DashboardState(
    val sports: List<Sport> = emptyList(),
    val competitions: List<Competition> = emptyList(),
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = true
)

class SportsViewModel(
    private val repository: SportsRepository
) : ViewModel() {

    // We combine all 3 flows.
    // Because the SportsRepository emits cache data first, then network data,
    // this state will update multiple ties rapidly (progressive UI).

    val uiState: StateFlow<DashboardState> = combine(
        repository.getSports().onStart { emit(emptyList()) },
        repository.getCompetitions().onStart { emit(emptyList()) },
        repository.getMatches().onStart { emit(emptyList()) }
    ) { sports, competitions, matches ->
        DashboardState(
            sports = sports,
            competitions = competitions,
            matches = matches,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState(isLoading = true)
    )
}