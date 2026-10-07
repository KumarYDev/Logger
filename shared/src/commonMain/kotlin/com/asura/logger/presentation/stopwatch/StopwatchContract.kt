package com.asura.logger.presentation.stopwatch

import com.asura.logger.core.mvi.UiEffect
import com.asura.logger.core.mvi.UiIntent
import com.asura.logger.core.mvi.UiState
import com.asura.logger.domain.model.Lap

/**
 * ============================================================================
 * MVI Contract: Stopwatch
 * ============================================================================
 *
 * Defines the complete state, supported user intents, and side effects
 * for the Stopwatch screen.
 */

/**
 * Immutable UI State representing the stopwatch.
 *
 * @property isRunning True if the stopwatch ticker is currently active.
 * @property elapsedMillis Total elapsed milliseconds.
 * @property laps List of recorded [Lap] entities in reverse chronological order.
 */
data class StopwatchState(
    val isRunning: Boolean = false,
    val elapsedMillis: Long = 0L,
    val laps: List<Lap> = emptyList()
) : UiState {

    /**
     * Reset is only enabled when the stopwatch is paused/stopped and has elapsed time.
     */
    val canReset: Boolean
        get() = elapsedMillis > 0L && !isRunning

    /**
     * Lap can only be pressed while the stopwatch is actively running.
     */
    val canLap: Boolean
        get() = isRunning

    /**
     * Duration of the currently active/in-progress lap.
     */
    val currentLapDurationMillis: Long
        get() {
            val previousLapsTotal = laps.sumOf { it.lapDurationMillis }
            return (elapsedMillis - previousLapsTotal).coerceAtLeast(0L)
        }
}

/**
 * User Intents (Actions) for the Stopwatch screen.
 */
sealed interface StopwatchIntent : UiIntent {
    /** User clicked Start / Resume button. */
    data object Start : StopwatchIntent

    /** User clicked Pause button. */
    data object Pause : StopwatchIntent

    /** User clicked Reset button. */
    data object Reset : StopwatchIntent

    /** User clicked Lap button. */
    data object LapRecorded : StopwatchIntent
}

/**
 * Single-fire Side Effects emitted from StopwatchViewModel.
 */
sealed interface StopwatchEffect : UiEffect {
    data class LapAdded(val lap: Lap) : StopwatchEffect
    data object ResetComplete : StopwatchEffect
    data object Started : StopwatchEffect
    data object Paused : StopwatchEffect
}
