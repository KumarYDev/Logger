package com.asura.logger.domain.model

/**
 * ============================================================================
 * Domain Model: Lap
 * ============================================================================
 *
 * Pure business model representing a single lap recorded during stopwatch execution.
 * Free from UI or framework dependencies.
 *
 * @property lapNumber Sequential identifier of the lap (1-indexed).
 * @property lapDurationMillis Time elapsed exclusively during this specific lap.
 * @property totalElapsedMillis Cumulative time of the stopwatch when this lap was captured.
 */
data class Lap(
    val lapNumber: Int,
    val lapDurationMillis: Long,
    val totalElapsedMillis: Long
)
