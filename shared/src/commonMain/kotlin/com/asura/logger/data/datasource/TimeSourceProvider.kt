package com.asura.logger.data.datasource

import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * ============================================================================
 * Data Source: TimeSourceProvider
 * ============================================================================
 *
 * Encapsulates monotonic high-precision time reading across all KMP platforms
 * (Android, iOS, Desktop JVM, JS, Wasm).
 */
interface TimeSourceProvider {
    fun markNow(): TimeMark
}

class MonotonicTimeSourceProvider(
    private val timeSource: TimeSource = TimeSource.Monotonic
) : TimeSourceProvider {
    override fun markNow(): TimeMark = timeSource.markNow()
}
