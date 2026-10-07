package com.asura.logger.domain.usecase.timer

import com.asura.logger.domain.model.TimerDuration

/**
 * ============================================================================
 * Use Case: NormalizeDurationUseCase
 * ============================================================================
 *
 * Validates, bounds-checks, and normalizes hours, minutes, and seconds input
 * or converts added seconds into a proper [TimerDuration] entity.
 */
class NormalizeDurationUseCase {

    /**
     * Clamps and normalizes individual H:M:S units.
     */
    fun fromHms(hours: Int, minutes: Int, seconds: Int): TimerDuration {
        val clampedHours = hours.coerceIn(0, 99)
        val clampedMinutes = minutes.coerceIn(0, 59)
        val clampedSeconds = seconds.coerceIn(0, 59)
        return TimerDuration(clampedHours, clampedMinutes, clampedSeconds)
    }

    /**
     * Converts a raw second count into a normalized [TimerDuration].
     */
    fun fromTotalSeconds(totalSeconds: Long): TimerDuration {
        val nonNegative = totalSeconds.coerceAtLeast(0L)
        val hours = (nonNegative / 3600).toInt().coerceIn(0, 99)
        val minutes = ((nonNegative % 3600) / 60).toInt()
        val seconds = (nonNegative % 60).toInt()
        return TimerDuration(hours, minutes, seconds)
    }

    /**
     * Adds an offset in seconds to an existing [TimerDuration].
     */
    fun addSeconds(current: TimerDuration, additionalSeconds: Long): TimerDuration {
        val currentTotalSec = current.totalMillis / 1000L
        val newTotalSec = currentTotalSec + additionalSeconds
        return fromTotalSeconds(newTotalSec)
    }
}
