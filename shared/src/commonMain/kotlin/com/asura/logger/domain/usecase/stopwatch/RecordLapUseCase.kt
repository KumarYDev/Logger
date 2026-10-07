package com.asura.logger.domain.usecase.stopwatch

import com.asura.logger.domain.model.Lap
import com.asura.logger.domain.repository.StopwatchRepository

/**
 * ============================================================================
 * Use Case: RecordLapUseCase
 * ============================================================================
 *
 * Encapsulates the business rule for calculating a new lap:
 * - Next lap number = existing laps count + 1
 * - Current lap duration = total elapsed time - sum of previous laps
 * - Persists and returns the new [Lap] domain entity.
 */
class RecordLapUseCase(
    private val repository: StopwatchRepository
) {
    /**
     * Executes the lap calculation and storage.
     *
     * @param currentElapsedMillis The total cumulative elapsed time of the stopwatch.
     * @return The newly generated [Lap].
     */
    operator fun invoke(currentElapsedMillis: Long): Lap {
        val existingLaps = repository.getLaps()
        val previousTotal = existingLaps.sumOf { it.lapDurationMillis }
        val lapDuration = (currentElapsedMillis - previousTotal).coerceAtLeast(0L)
        val nextLapNumber = existingLaps.size + 1

        val newLap = Lap(
            lapNumber = nextLapNumber,
            lapDurationMillis = lapDuration,
            totalElapsedMillis = currentElapsedMillis
        )

        repository.addLap(newLap)
        return newLap
    }
}
