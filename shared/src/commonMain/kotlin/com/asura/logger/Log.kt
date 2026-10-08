package com.asura.logger

import co.touchlab.kermit.Logger

/**
 * A centralized logging utility for the application across all multiplatform targets.
 *
 * This object wraps the Kermit logger to provide a consistent logging interface.
 * All logs emitted through this utility will use the base tag [APP_TAG].
 * If a specific `tag` is provided, it will be appended to the base tag with an underscore (e.g., `[APP_TAG]_tag`).
 * 
 * Example usage:
 * ```
 * Log.d("User logged in successfully") // Tag: ASURA_LOGGER
 * Log.e("Failed to fetch data", "NETWORK", exception) // Tag: ASURA_LOGGER_NETWORK
 * ```
 */
object Log {
    /** The fallback tag used when no specific tag is provided. */
    const val APP_TAG = "ASURA_LOGGER"

    /**
     * Helper function to format the tag.
     * Appends the specific [tag] to [APP_TAG] using an underscore, or returns [APP_TAG] if null.
     */
    private fun formatTag(tag: String?): String = 
        if (tag == null) APP_TAG else "${APP_TAG}_$tag"

    /**
     * Logs a Verbose level message. Use this for detailed, potentially high-volume logs.
     *
     * @param message The message to log.
     * @param tag The specific tag for the log (e.g., "NETWORK", "UI"). If null, only [APP_TAG] is used.
     *            If provided, it will be formatted as `ASURA_LOGGER_[tag]`.
     * @param throwable An optional exception to log alongside the message.
     */
    fun v(message: String, tag: String? = null, throwable: Throwable? = null) {
        Logger.v(throwable, formatTag(tag)) { message }
    }

    /**
     * Logs a Debug level message. Use this for development and debugging information.
     *
     * @param message The message to log.
     * @param tag The specific tag for the log (e.g., "NETWORK", "UI"). If null, only [APP_TAG] is used.
     *            If provided, it will be formatted as `ASURA_LOGGER_[tag]`.
     * @param throwable An optional exception to log alongside the message.
     */
    fun d(message: String, tag: String? = null, throwable: Throwable? = null) {
        Logger.d(throwable, formatTag(tag)) { message }
    }

    /**
     * Logs an Info level message. Use this for general app flow tracking and state changes.
     *
     * @param message The message to log.
     * @param tag The specific tag for the log (e.g., "NETWORK", "UI"). If null, only [APP_TAG] is used.
     *            If provided, it will be formatted as `ASURA_LOGGER_[tag]`.
     * @param throwable An optional exception to log alongside the message.
     */
    fun i(message: String, tag: String? = null, throwable: Throwable? = null) {
        Logger.i(throwable, formatTag(tag)) { message }
    }

    /**
     * Logs a Warning level message. Use this for unexpected events that don't necessarily cause a failure.
     *
     * @param message The message to log.
     * @param tag The specific tag for the log (e.g., "NETWORK", "UI"). If null, only [APP_TAG] is used.
     *            If provided, it will be formatted as `ASURA_LOGGER_[tag]`.
     * @param throwable An optional exception to log alongside the message.
     */
    fun w(message: String, tag: String? = null, throwable: Throwable? = null) {
        Logger.w(throwable, formatTag(tag)) { message }
    }

    /**
     * Logs an Error level message. Use this for handled exceptions or significant failures.
     *
     * @param message The message to log.
     * @param tag The specific tag for the log (e.g., "NETWORK", "UI"). If null, only [APP_TAG] is used.
     *            If provided, it will be formatted as `ASURA_LOGGER_[tag]`.
     * @param throwable An optional exception to log alongside the message.
     */
    fun e(message: String, tag: String? = null, throwable: Throwable? = null) {
        Logger.e(throwable, formatTag(tag)) { message }
    }

    /**
     * Logs an Assert (WTF - What a Terrible Failure) level message. Use this for conditions that should never happen.
     *
     * @param message The message to log.
     * @param tag The specific tag for the log (e.g., "NETWORK", "UI"). If null, only [APP_TAG] is used.
     *            If provided, it will be formatted as `ASURA_LOGGER_[tag]`.
     * @param throwable An optional exception to log alongside the message.
     */
    fun wtf(message: String, tag: String? = null, throwable: Throwable? = null) {
        Logger.a(throwable, formatTag(tag)) { message }
    }
}