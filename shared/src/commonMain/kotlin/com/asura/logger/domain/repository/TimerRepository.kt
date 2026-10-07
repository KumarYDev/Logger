package com.asura.logger.domain.repository

import com.asura.logger.domain.model.TimerDuration

/**
 * ============================================================================
 * Domain Repository Interface: TimerRepository
 * ============================================================================
 *
 * Defines the contract for persisting or retrieving default timer presets and configurations.
 */
interface TimerRepository {

    /**
     * Gets default timer duration configuration.
     */
    fun getDefaultDuration(): TimerDuration

    /**
     * Saves user's preferred default timer duration.
     */
    fun saveDefaultDuration(duration: TimerDuration)

    /**
     * Gets available quick-add presets in seconds (e.g., +30s, +60s, +300s, +600s).
     */
    fun getQuickPresetsSeconds(): List<Long>
}
