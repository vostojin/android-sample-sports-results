package com.sample.sportsresults.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.sportsresults.domain.Competition
import com.sample.sportsresults.domain.Match
import com.sample.sportsresults.domain.Sport
import com.sample.sportsresults.domain.SportsRepository
import kotlinx.coroutines.flow.*

enum class PrematchCategory(val label: String) {
    TODAY("Danas"),
    TOMORROW("Sutra"),
    WEEKEND("Vikend"),
    NEXT_WEEK("Sledeća nedelja"),
    LATER("Kasnije")
}

data class DashboardState(
    val selectedSportId: Int = 0,
    val selectedPrematchCategory: PrematchCategory = PrematchCategory.TODAY,
    val sports: List<Sport> = emptyList(),
    val competitions: List<Competition> = emptyList(),
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = true
)

class SportsViewModel(
    private val repository: SportsRepository
) : ViewModel() {

    private val selectedSportIdFlow = MutableStateFlow(1)
    private val selectedPrematchFlow = MutableStateFlow(PrematchCategory.TOMORROW)

    fun setSportId(id: Int) {
        selectedSportIdFlow.value = id
    }

    fun setPrematchPeriod(period: PrematchCategory) {
        selectedPrematchFlow.value = period
    }

    // We combine all 3 flows.
    // Because the SportsRepository emits cache data first, then network data,
    // this state will update multiple ties rapidly (progressive UI).

    val uiState: StateFlow<DashboardState> = combine(
        selectedSportIdFlow,
        selectedPrematchFlow,
        repository.getSports().onStart { emit(emptyList()) },
        repository.getCompetitions().onStart { emit(emptyList()) },
        repository.getMatches().onStart { emit(emptyList()) }
    ) { selectedSportId, selectedPrematchPeriod, sports, competitions, matches ->
        DashboardState(
            selectedSportId = selectedSportId,
            selectedPrematchCategory = selectedPrematchPeriod,
            sports = sports,
            competitions = competitions.filter { it.sportId == selectedSportId },
            matches = matches.filter { it.sportId == selectedSportId },
            isLoading = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState(isLoading = true)
    )
    
}