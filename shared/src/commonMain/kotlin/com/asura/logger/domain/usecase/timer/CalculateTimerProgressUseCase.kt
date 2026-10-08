package com.asura.logger.domain.usecase.timer

import com.asura.logger.Log

/**
 * ============================================================================
 * Use Case: CalculateTimerProgressUseCase
 * ============================================================================
 *
 * Encapsulates the business calculation for timer countdown percentage and fraction.
 */
class CalculateTimerProgressUseCase {

    /**
     * Calculates the normalized progress (0.0f to 1.0f) representing the remaining fraction.
     *
     * @param remainingMillis Milliseconds remaining in countdown.
     * @param totalDurationMillis Total countdown duration in milliseconds.
     * @return Normalized float in range [0.0f, 1.0f].
     */
    operator fun invoke(remainingMillis: Long, totalDurationMillis: Long): Float {
        if (totalDurationMillis <= 0L) {
            Log.d("Progress calculation with non-positive totalDurationMillis=$totalDurationMillis -> 0.0f", tag = "CalculateTimerProgressUseCase_invoke()")
            return 0f
        }
        val progress = (remainingMillis.toFloat() / totalDurationMillis.toFloat()).coerceIn(0f, 1f)
        Log.d("Calculated progress: remainingMillis=$remainingMillis, totalDurationMillis=$totalDurationMillis -> progress=$progress", tag = "CalculateTimerProgressUseCase_invoke()")
        return progress
    }
}
