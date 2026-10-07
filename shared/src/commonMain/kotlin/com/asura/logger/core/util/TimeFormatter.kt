package com.asura.logger.core.util

/**
 * ============================================================================
 * Time Formatting Utility
 * ============================================================================
 *
 * Provides pure formatting functions for translating raw millisecond timestamps
 * into user-friendly digital displays across all platforms.
 */
object TimeFormatter {

    /**
     * Formats milliseconds into standard digital stopwatch format:
     * - `MM:SS.cs` (e.g. `02:15.84`) when hours == 0
     * - `HH:MM:SS.cs` (e.g. `01:05:12.45`) when hours > 0
     *
     * @param millis Elapsed time in milliseconds.
     * @return Formatted digital string.
     */
    fun formatStopwatch(millis: Long): String {
        val totalCentis = millis / 10
        val centis = totalCentis % 100
        val totalSeconds = totalCentis / 100
        val seconds = totalSeconds % 60
        val totalMinutes = totalSeconds / 60
        val minutes = totalMinutes % 60
        val hours = totalMinutes / 60

        val centisStr = padTwoDigits(centis)
        val secondsStr = padTwoDigits(seconds)
        val minutesStr = padTwoDigits(minutes)

        return if (hours > 0) {
            val hoursStr = padTwoDigits(hours)
            "$hoursStr:$minutesStr:$secondsStr.$centisStr"
        } else {
            "$minutesStr:$secondsStr.$centisStr"
        }
    }

    /**
     * Formats remaining countdown milliseconds into standard timer format:
     * - `MM:SS` (e.g. `05:00`) when hours == 0
     * - `HH:MM:SS` (e.g. `01:30:00`) when hours > 0
     *
     * Note: Uses slight ceiling rounding so the seconds counter feels natural to human eyes.
     *
     * @param millis Remaining duration in milliseconds.
     * @return Formatted timer string.
     */
    fun formatTimer(millis: Long): String {
        val totalSeconds = (millis + 999) / 1000 // Round up slightly for smooth countdown visual
        val seconds = totalSeconds % 60
        val totalMinutes = totalSeconds / 60
        val minutes = totalMinutes % 60
        val hours = totalMinutes / 60

        val secondsStr = padTwoDigits(seconds)
        val minutesStr = padTwoDigits(minutes)

        return if (hours > 0) {
            val hoursStr = padTwoDigits(hours)
            "$hoursStr:$minutesStr:$secondsStr"
        } else {
            "$minutesStr:$secondsStr"
        }
    }

    /**
     * Formats discrete hour, minute, and second components into `HH:MM:SS`.
     */
    fun formatHms(hours: Int, minutes: Int, seconds: Int): String {
        return "${padTwoDigits(hours.toLong())}:${padTwoDigits(minutes.toLong())}:${padTwoDigits(seconds.toLong())}"
    }

    private fun padTwoDigits(value: Long): String {
        val str = value.coerceAtLeast(0).toString()
        return if (str.length < 2) "0$str" else str
    }
}
