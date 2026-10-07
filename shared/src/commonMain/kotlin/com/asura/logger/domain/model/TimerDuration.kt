package com.asura.logger.domain.model

/**
 * ============================================================================
 * Domain Model: TimerDuration
 * ============================================================================
 *
 * Represents discrete user-selected time units for configuring a countdown timer.
 *
 * @property hours Configured hours (0..99).
 * @property minutes Configured minutes (0..59).
 * @property seconds Configured seconds (0..59).
 */
data class TimerDuration(
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0
) {
    /**
     * Total calculated duration in milliseconds.
     */
    val totalMillis: Long
        get() = (hours * 3600L + minutes * 60L + seconds) * 1000L

    /**
     * Checks if this duration represents a positive non-zero period.
     */
    val isValid: Boolean
        get() = totalMillis > 0L
}
