package com.asura.logger.data.repository

import com.asura.logger.domain.model.Lap
import com.asura.logger.domain.repository.StopwatchRepository

/**
 * ============================================================================
 * Data Repository Implementation: StopwatchRepositoryImpl
 * ============================================================================
 *
 * Concrete implementation of [StopwatchRepository] holding recorded laps in memory.
 */
class StopwatchRepositoryImpl : StopwatchRepository {

    private val laps = mutableListOf<Lap>()

    override fun getLaps(): List<Lap> {
        return laps.toList()
    }

    override fun addLap(lap: Lap) {
        laps.add(lap)
    }

    override fun clearLaps() {
        laps.clear()
    }
}
