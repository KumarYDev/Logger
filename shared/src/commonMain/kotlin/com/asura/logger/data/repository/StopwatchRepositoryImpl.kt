package com.asura.logger.data.repository

import com.asura.logger.Log
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
        val result = laps.toList()
        Log.d("Retrieved ${result.size} laps from repository", tag = "StopwatchRepositoryImpl_getLaps()")
        return result
    }

    override fun addLap(lap: Lap) {
        laps.add(lap)
        Log.i("Appended new lap to repository: $lap (total laps: ${laps.size})", tag = "StopwatchRepositoryImpl_addLap()")
    }

    override fun clearLaps() {
        val previousCount = laps.size
        laps.clear()
        Log.i("Cleared all $previousCount laps from repository", tag = "StopwatchRepositoryImpl_clearLaps()")
    }
}
