package com.asura.logger.domain.usecase.timer

import com.asura.logger.Log
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
        val result = TimerDuration(clampedHours, clampedMinutes, clampedSeconds)
        Log.d("Normalized from HMS: ($hours, $minutes, $seconds) -> $result", tag = "NormalizeDurationUseCase_fromHms()")
        return result
    }

    /**
     * Converts a raw second count into a normalized [TimerDuration].
     */
    fun fromTotalSeconds(totalSeconds: Long): TimerDuration {
        val nonNegative = totalSeconds.coerceAtLeast(0L)
        val hours = (nonNegative / 3600).toInt().coerceIn(0, 99)
        val minutes = ((nonNegative % 3600) / 60).toInt()
        val seconds = (nonNegative % 60).toInt()
        val result = TimerDuration(hours, minutes, seconds)
        Log.d("Normalized from total seconds: $totalSeconds -> $result", tag = "NormalizeDurationUseCase_fromTotalSeconds()")
        return result
    }

    /**
     * Adds an offset in seconds to an existing [TimerDuration].
     */
    fun addSeconds(current: TimerDuration, additionalSeconds: Long): TimerDuration {
        val currentTotalSec = current.totalMillis / 1000L
        val newTotalSec = currentTotalSec + additionalSeconds
        val result = fromTotalSeconds(newTotalSec)
        Log.d("Added $additionalSeconds seconds to $current -> $result", tag = "NormalizeDurationUseCase_addSeconds()")
        return result
    }
}
