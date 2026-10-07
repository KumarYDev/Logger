package com.asura.logger.presentation.timer

import com.asura.logger.core.mvi.UiEffect
import com.asura.logger.core.mvi.UiIntent
import com.asura.logger.core.mvi.UiState
import com.asura.logger.domain.model.TimerDuration

/**
 * ============================================================================
 * MVI Contract: Timer
 * ============================================================================
 *
 * Defines the state, supported user intents, and side effects
 * for the Countdown Timer screen.
 */

/**
 * Immutable UI State representing the Timer.
 *
 * @property duration Configured time duration [TimerDuration].
 * @property totalDurationMillis Total countdown duration in milliseconds.
 * @property remainingMillis Remaining countdown time in milliseconds.
 * @property isRunning True if timer is actively counting down.
 * @property isPaused True if timer is paused mid-countdown.
 * @property isFinished True if countdown reached zero.
 * @property progress Fraction from 0.0f to 1.0f representing progress.
 * @property availablePresets List of quick presets in seconds (e.g., 30s, 60s).
 */
data class TimerState(
    val duration: TimerDuration = TimerDuration(hours = 0, minutes = 0, seconds = 0),
    val totalDurationMillis: Long = 60_000L,
    val remainingMillis: Long = 60_000L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val progress: Float = 1f,
    val availablePresets: List<Long> = listOf(30L, 60L, 300L, 600L)
) : UiState {

    /**
     * True when the user is adjusting duration and the timer is not running/paused.
     */
    val isConfiguring: Boolean
        get() = !isRunning && !isPaused && !isFinished

    /**
     * True if timer can be started.
     */
    val canStart: Boolean
        get() = !isRunning && (remainingMillis > 0L || duration.isValid)
}

/**
 * User Intents (Actions) for the Timer screen.
 */
sealed interface TimerIntent : UiIntent {
    /** Sets duration by specific hours, minutes, seconds. */
    data class SetDuration(val hours: Int, val minutes: Int, val seconds: Int) : TimerIntent

    /** Sets specific hours without altering minutes or seconds. */
    data class SetHours(val hours: Int) : TimerIntent

    /** Sets specific minutes without altering hours or seconds. */
    data class SetMinutes(val minutes: Int) : TimerIntent

    /** Sets specific seconds without altering hours or minutes. */
    data class SetSeconds(val seconds: Int) : TimerIntent

    /** Adds additional seconds (+30s, +1m, etc.). */
    data class AddSeconds(val seconds: Long) : TimerIntent

    /** User clicked Start. */
    data object Start : TimerIntent

    /** User clicked Pause. */
    data object Pause : TimerIntent

    /** User clicked Resume. */
    data object Resume : TimerIntent

    /** User clicked Reset. */
    data object Reset : TimerIntent

    /** User dismissed the completed timer banner. */
    data object DismissFinished : TimerIntent
}

/**
 * Single-fire Side Effects emitted from TimerViewModel.
 */
sealed interface TimerEffect : UiEffect {
    data object TimerFinished : TimerEffect
    data object Started : TimerEffect
    data object Paused : TimerEffect
    data object ResetCompleted : TimerEffect
}
