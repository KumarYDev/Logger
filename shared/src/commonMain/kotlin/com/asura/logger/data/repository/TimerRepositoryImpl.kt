package com.asura.logger.data.repository

import com.asura.logger.Log
import com.asura.logger.domain.model.TimerDuration
import com.asura.logger.domain.repository.TimerRepository

/**
 * ============================================================================
 * Data Repository Implementation: TimerRepositoryImpl
 * ============================================================================
 *
 * Concrete implementation of [TimerRepository] supplying defaults and presets.
 */
class TimerRepositoryImpl : TimerRepository {

    private var defaultDuration = TimerDuration(hours = 0, minutes = 0, seconds = 0)

    override fun getDefaultDuration(): TimerDuration {
        Log.d("Retrieving default duration: $defaultDuration", tag = "TimerRepositoryImpl_getDefaultDuration()")
        return defaultDuration
    }

    override fun saveDefaultDuration(duration: TimerDuration) {
        Log.i("Saving default duration: $duration (previous: $defaultDuration)", tag = "TimerRepositoryImpl_saveDefaultDuration()")
        defaultDuration = duration
    }

    override fun getQuickPresetsSeconds(): List<Long> {
        val presets = listOf(30L, 60L, 300L, 600L) // +30s, +1m, +5m, +10m
        Log.d("Retrieving quick presets: $presets", tag = "TimerRepositoryImpl_getQuickPresetsSeconds()")
        return presets
    }
}
