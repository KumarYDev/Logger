package com.asura.logger.data.datasource

import com.asura.logger.Log
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
    override fun markNow(): TimeMark {
        Log.d("Marking monotonic time", tag = "MonotonicTimeSourceProvider_markNow()")
        return timeSource.markNow()
    }
}
