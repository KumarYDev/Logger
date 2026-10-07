package com.asura.logger.domain.repository

import com.asura.logger.domain.model.Lap

/**
 * ============================================================================
 * Domain Repository Interface: StopwatchRepository
 * ============================================================================
 *
 * Defines the contract for managing stopwatch lap history and time calculation.
 * Decoupled from any storage mechanism or UI framework.
 */
interface StopwatchRepository {

    /**
     * Retrieves the current list of recorded laps.
     */
    fun getLaps(): List<Lap>

    /**
     * Adds a newly recorded lap to the repository.
     */
    fun addLap(lap: Lap)

    /**
     * Clears all recorded laps.
     */
    fun clearLaps()
}
