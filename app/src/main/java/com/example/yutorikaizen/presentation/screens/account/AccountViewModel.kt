package com.example.yutorikaizen.presentation.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.data.local.SkinPreferences
import com.example.yutorikaizen.data.local.UsageTracker
import com.example.yutorikaizen.data.local.db.AppDatabase
import com.example.yutorikaizen.data.local.entity.DailyCheckInEntity
import com.example.yutorikaizen.data.local.entity.SurgeEventEntity
import com.example.yutorikaizen.domain.model.ALL_SKINS
import com.example.yutorikaizen.domain.model.DopaminePrograms
import com.example.yutorikaizen.domain.model.Expense
import com.example.yutorikaizen.domain.model.FrogSkin
import com.example.yutorikaizen.domain.repository.DailyCheckInRepository
import com.example.yutorikaizen.domain.repository.ExpenseRepository
import com.example.yutorikaizen.domain.repository.SurgeEventRepository
import com.example.yutorikaizen.domain.repository.UserProfileRepository
import com.example.yutorikaizen.domain.usecase.CurrentPhaseUseCase
import com.example.yutorikaizen.domain.usecase.NciCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class AccountUiState(
    val skins: List<FrogSkin> = ALL_SKINS,
    val selectedSkinId: String = SkinPreferences.DEFAULT_SKIN,
    val pendingRestart: Boolean = false,
    val dayNumber: Int = 1,
    val phaseNumber: Int = 1,
    val phaseName: String = "",
    val difficultyLabel: String = "",
    val programName: String = "",
    val nciScore: Float = 0f,
    val currentStreak: Int = 0,
    val usageTrackingEnabled: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val showResetJourneyConfirm: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val monthlyTotal: Double = 0.0,
    val categoryBreakdown: Map<String, Double> = emptyMap(),
    val showAddExpense: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val skinPreferences: SkinPreferences,
    private val appDatabase: AppDatabase,
    private val userProfileRepository: UserProfileRepository,
    private val dailyCheckInRepository: DailyCheckInRepository,
    private val surgeEventRepository: SurgeEventRepository,
    private val currentPhaseUseCase: CurrentPhaseUseCase,
    private val nciCalculator: NciCalculator,
    private val expenseRepository: ExpenseRepository,
    private val usageTracker: UsageTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    private val activeSkinId = skinPreferences.getSelectedSkinIdBlocking()

    init {
        viewModelScope.launch {
            skinPreferences.selectedSkinId.collect { saved ->
                _uiState.update { it.copy(selectedSkinId = saved, pendingRestart = saved != activeSkinId) }
            }
        }
        collectSettings()
        loadProfileData()
        collectExpenses()
    }

    private fun collectSettings() {
        viewModelScope.launch {
            skinPreferences.usageTrackingEnabled.collect { enabled ->
                _uiState.update { it.copy(usageTrackingEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            skinPreferences.notificationsEnabled.collect { enabled ->
                _uiState.update { it.copy(notificationsEnabled = enabled) }
            }
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                userProfileRepository.observeProfile(),
                skinPreferences.currentJourneyDay,
                dailyCheckInRepository.observeLast14Days()
            ) { profile, day, checkIns -> Triple(profile, day, checkIns) }
                .collect { (profile, day, checkIns) ->
                    if (profile != null) {
                        val journeyState = currentPhaseUseCase.execute(day, profile.difficultyLevel)
                        val program = DopaminePrograms.byId(profile.selectedProgramId)

                        val since14Ms = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(14)
                        val surgeEvents = surgeEventRepository.getEventsSince(since14Ms)
                        
                        val checkInEnts = checkIns.map { ci ->
                            DailyCheckInEntity(
                                id                = ci.id,
                                epochDay          = ci.epochDay,
                                phaseNumber       = ci.phaseNumber,
                                completedHabitIds = JSONArray(ci.completedHabitIds).toString(),
                                focusMinutes      = ci.focusMinutes,
                                nciScore          = ci.nciScore
                            )
                        }
                        val surgeEnts = surgeEvents.map { se ->
                            SurgeEventEntity(
                                id              = se.id,
                                timestamp       = se.timestamp,
                                durationSeconds = se.durationSeconds,
                                outcome         = se.outcome.name,
                                methodUsed      = se.methodUsed.name,
                                emotionLogged   = se.emotionLogged
                            )
                        }

                        val nci = nciCalculator.calculate(checkInEnts, surgeEnts)

                        var streak = 0
                        for (ci in checkIns.sortedByDescending { it.epochDay }) {
                            if (ci.nciScore >= 0.5f) streak++ else break
                        }

                        _uiState.update { 
                            it.copy(
                                dayNumber = day,
                                phaseNumber = journeyState.currentPhase.number,
                                phaseName = journeyState.currentPhase.name,
                                difficultyLabel = profile.difficultyLevel.label,
                                programName = program?.name ?: "",
                                nciScore = nci,
                                currentStreak = streak
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                dayNumber = 1,
                                phaseNumber = 1,
                                phaseName = "",
                                difficultyLabel = "",
                                programName = "",
                                nciScore = 0f,
                                currentStreak = 0
                            )
                        }
                    }
                }
        }
    }

    fun resetJourney() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            skinPreferences.setCurrentJourneyDay(1)
            userProfileRepository.resetJourney(now)
            dailyCheckInRepository.deleteAll()
            surgeEventRepository.deleteAll()
            _uiState.update {
                it.copy(
                    dayNumber = 1,
                    phaseNumber = 1,
                    phaseName = "Reset & Detox",
                    nciScore = 0f,
                    currentStreak = 0
                )
            }
        }
    }

    fun selectSkin(skin: FrogSkin) {
        viewModelScope.launch {
            skinPreferences.setSkin(skin.id)
        }
    }

    fun toggleUsageTracking(enabled: Boolean) {
        viewModelScope.launch {
            skinPreferences.setUsageTrackingEnabled(enabled)
            if (enabled) {
                withContext(Dispatchers.IO) {
                    usageTracker.syncUsageForToday()
                }
            }
        }
    }

    fun checkUsagePermissionOnResume() {
        val hasPerm = usageTracker.hasPermission()
        viewModelScope.launch {
            skinPreferences.setUsageTrackingEnabled(hasPerm)
            if (hasPerm) {
                withContext(Dispatchers.IO) {
                    usageTracker.syncUsageForToday()
                }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            skinPreferences.setNotificationsEnabled(enabled)
        }
    }

    fun showResetJourneyConfirmation() {
        _uiState.update { it.copy(showResetJourneyConfirm = true) }
    }

    fun dismissResetJourneyConfirmation() {
        _uiState.update { it.copy(showResetJourneyConfirm = false) }
    }

    fun confirmResetJourney() {
        dismissResetJourneyConfirmation()
        resetJourney()
    }

    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirm = true) }
    }

    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    fun deleteAccount(onComplete: () -> Unit) {
        if (!uiState.value.showDeleteConfirm) {
            showDeleteConfirmation()
            return
        }
        viewModelScope.launch {
            skinPreferences.clearAll()
            withContext(Dispatchers.IO) {
                appDatabase.clearAllTables()
            }
            _uiState.update {
                it.copy(
                    showDeleteConfirm = false,
                    dayNumber = 1,
                    phaseNumber = 1,
                    phaseName = "",
                    difficultyLabel = "",
                    programName = "",
                    nciScore = 0f,
                    currentStreak = 0
                )
            }
            onComplete()
        }
    }

    // ── Expense Tracker ────────────────────────────────────────────────────────

    private fun collectExpenses() {
        viewModelScope.launch {
            combine(
                expenseRepository.getRecentExpenses(),
                expenseRepository.getExpensesForCurrentMonth()
            ) { recent, thisMonth ->
                val total = thisMonth.sumOf { it.amount }
                val breakdown = thisMonth.groupBy { it.category }
                    .mapValues { (_, list) -> list.sumOf { it.amount } }
                Triple(recent, total, breakdown)
            }.collect { (recent, total, breakdown) ->
                _uiState.update {
                    it.copy(
                        expenses = recent,
                        monthlyTotal = total,
                        categoryBreakdown = breakdown
                    )
                }
            }
        }
    }

    private fun currentEpochMonth(): Int {
        val cal = Calendar.getInstance()
        return (cal.get(Calendar.YEAR) * 12) + cal.get(Calendar.MONTH)
    }

    fun showAddExpenseDialog() {
        _uiState.update { it.copy(showAddExpense = true) }
    }

    fun dismissAddExpenseDialog() {
        _uiState.update { it.copy(showAddExpense = false) }
    }

    fun addExpense(amount: Double, category: String, note: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance()
            val epochMonth = (cal.get(Calendar.YEAR) * 12) + cal.get(Calendar.MONTH)
            expenseRepository.addExpense(
                Expense(
                    id = 0,
                    amount = amount,
                    category = category,
                    note = note.ifBlank { null },
                    timestamp = now,
                    epochMonth = epochMonth
                )
            )
            dismissAddExpenseDialog()
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }
}
