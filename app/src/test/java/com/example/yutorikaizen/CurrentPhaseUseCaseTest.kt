package com.example.yutorikaizen

import com.example.yutorikaizen.domain.model.DifficultyLevel
import com.example.yutorikaizen.domain.usecase.CurrentPhaseUseCase
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class CurrentPhaseUseCaseTest {

    private val useCase = CurrentPhaseUseCase()
    private val zone = ZoneId.of("UTC")

    @Test
    fun sameDayReturnsDay1() {
        val startMs = LocalDate.of(2026, 11, 10).atStartOfDay(zone).toInstant().toEpochMilli()
        val nowMs = LocalDate.of(2026, 11, 10).atStartOfDay(zone).toInstant().toEpochMilli() + 3600000L

        val day = useCase.calculateDayNumber(startMs, nowMs, zone)
        assertEquals(1, day)
    }

    @Test
    fun nextDayReturnsDay2EvenIfLessThan24Hours() {
        // Started 11:30 PM on Nov 10
        val startMs = LocalDate.of(2026, 11, 10).atTime(23, 30).atZone(zone).toInstant().toEpochMilli()
        // Checked 8:00 AM on Nov 11 (only 8.5 hours later)
        val nowMs = LocalDate.of(2026, 11, 11).atTime(8, 0).atZone(zone).toInstant().toEpochMilli()

        val day = useCase.calculateDayNumber(startMs, nowMs, zone)
        assertEquals(2, day)
    }

    @Test
    fun multipleDaysPassReturnsAccurateDay() {
        val startMs = LocalDate.of(2026, 11, 1).atStartOfDay(zone).toInstant().toEpochMilli()
        val nowMs = LocalDate.of(2026, 11, 16).atStartOfDay(zone).toInstant().toEpochMilli()

        val day = useCase.calculateDayNumber(startMs, nowMs, zone)
        assertEquals(16, day)
    }

    @Test
    fun futureOrZeroDateReturnsAtLeastDay1() {
        assertEquals(1, useCase.calculateDayNumber(0L))
        val startMs = LocalDate.of(2026, 11, 20).atStartOfDay(zone).toInstant().toEpochMilli()
        val nowMs = LocalDate.of(2026, 11, 16).atStartOfDay(zone).toInstant().toEpochMilli()
        assertEquals(1, useCase.calculateDayNumber(startMs, nowMs, zone))
    }

    @Test
    fun executeDerivesPhaseCorrectly() {
        val startMs = LocalDate.of(2026, 11, 1).atStartOfDay(zone).toInstant().toEpochMilli()
        val state = useCase.execute(dayNumber = 5, difficulty = DifficultyLevel.GENTLE)
        assertEquals(5, state.dayNumber)
        assertEquals(1, state.currentPhase.number)
    }
}
