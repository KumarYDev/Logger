package com.asura.logger.domain.usecase.stopwatch

import com.asura.logger.Log
import com.asura.logger.domain.repository.StopwatchRepository

/**
 * ============================================================================
 * Use Case: ResetStopwatchUseCase
 * ============================================================================
 *
 * Encapsulates the business rule for resetting stopwatch history.
 */
class ResetStopwatchUseCase(
    private val repository: StopwatchRepository
) {
    /**
     * Executes the reset logic, clearing repository state.
     */
    operator fun invoke() {
        Log.i("Executing stopwatch reset, clearing laps repository", tag = "ResetStopwatchUseCase_invoke()")
        repository.clearLaps()
    }
}
