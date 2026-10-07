package com.asura.logger.presentation.stopwatch

import androidx.lifecycle.viewModelScope
import com.asura.logger.core.mvi.MviViewModel
import com.asura.logger.data.datasource.MonotonicTimeSourceProvider
import com.asura.logger.data.datasource.TimeSourceProvider
import com.asura.logger.data.repository.StopwatchRepositoryImpl
import com.asura.logger.domain.repository.StopwatchRepository
import com.asura.logger.domain.usecase.stopwatch.RecordLapUseCase
import com.asura.logger.domain.usecase.stopwatch.ResetStopwatchUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.TimeMark

/**
 * ============================================================================
 * Presentation ViewModel: StopwatchViewModel
 * ============================================================================
 *
 * Implements MVI by processing [StopwatchIntent]s, executing Domain UseCases,
 * updating [StopwatchState], and publishing [StopwatchEffect]s.
 */
class StopwatchViewModel(
    private val stopwatchRepository: StopwatchRepository = StopwatchRepositoryImpl(),
    private val recordLapUseCase: RecordLapUseCase = RecordLapUseCase(stopwatchRepository),
    private val resetStopwatchUseCase: ResetStopwatchUseCase = ResetStopwatchUseCase(stopwatchRepository),
    private val timeSourceProvider: TimeSourceProvider = MonotonicTimeSourceProvider()
) : MviViewModel<StopwatchState, StopwatchIntent, StopwatchEffect>(StopwatchState()) {

    private var tickerJob: Job? = null
    private var startMark: TimeMark? = null
    private var baseElapsedMillis: Long = 0L

    /**
     * Central intent dispatcher: Processes all incoming user actions from the UI.
     */
    override fun handleIntent(intent: StopwatchIntent) {
        when (intent) {
            is StopwatchIntent.Start -> startStopwatch()
            is StopwatchIntent.Pause -> pauseStopwatch()
            is StopwatchIntent.Reset -> resetStopwatch()
            is StopwatchIntent.LapRecorded -> recordLap()
        }
    }

    private fun startStopwatch() {
        if (currentState.isRunning) return

        baseElapsedMillis = currentState.elapsedMillis
        startMark = timeSourceProvider.markNow()

        setState { copy(isRunning = true) }
        sendEffect(StopwatchEffect.Started)

        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                val mark = startMark ?: break
                val currentTotal = baseElapsedMillis + mark.elapsedNow().inWholeMilliseconds
                setState { copy(elapsedMillis = currentTotal) }
                delay(16) // ~60fps smooth digital refresh
            }
        }
    }

    private fun pauseStopwatch() {
        if (!currentState.isRunning) return

        tickerJob?.cancel()
        tickerJob = null

        val mark = startMark
        if (mark != null) {
            val finalElapsed = baseElapsedMillis + mark.elapsedNow().inWholeMilliseconds
            baseElapsedMillis = finalElapsed
            setState { copy(isRunning = false, elapsedMillis = finalElapsed) }
        } else {
            setState { copy(isRunning = false) }
        }
        startMark = null
        sendEffect(StopwatchEffect.Paused)
    }

    private fun resetStopwatch() {
        tickerJob?.cancel()
        tickerJob = null
        startMark = null
        baseElapsedMillis = 0L

        // Execute domain use case to clear repository records
        resetStopwatchUseCase()

        setState { StopwatchState() }
        sendEffect(StopwatchEffect.ResetComplete)
    }

    private fun recordLap() {
        if (!currentState.isRunning) return

        // Execute domain use case to calculate and store lap
        val newLap = recordLapUseCase(currentState.elapsedMillis)

        setState {
            copy(laps = listOf(newLap) + laps)
        }
        sendEffect(StopwatchEffect.LapAdded(newLap))
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }
}
