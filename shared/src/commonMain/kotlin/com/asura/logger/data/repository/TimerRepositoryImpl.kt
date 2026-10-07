package com.asura.logger.data.repository

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
        return defaultDuration
    }

    override fun saveDefaultDuration(duration: TimerDuration) {
        defaultDuration = duration
    }

    override fun getQuickPresetsSeconds(): List<Long> {
        return listOf(30L, 60L, 300L, 600L) // +30s, +1m, +5m, +10m
    }
}
