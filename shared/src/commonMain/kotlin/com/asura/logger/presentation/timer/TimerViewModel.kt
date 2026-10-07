package com.asura.logger.presentation.timer

import androidx.lifecycle.viewModelScope
import com.asura.logger.core.mvi.MviViewModel
import com.asura.logger.data.datasource.MonotonicTimeSourceProvider
import com.asura.logger.data.datasource.TimeSourceProvider
import com.asura.logger.data.repository.TimerRepositoryImpl
import com.asura.logger.domain.repository.TimerRepository
import com.asura.logger.domain.usecase.timer.CalculateTimerProgressUseCase
import com.asura.logger.domain.usecase.timer.NormalizeDurationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.TimeMark

/**
 * ============================================================================
 * Presentation ViewModel: TimerViewModel
 * ============================================================================
 *
 * Manages timer state, coordinates domain logic via UseCases,
 * and maintains the countdown ticker coroutine.
 */
class TimerViewModel(
    private val timerRepository: TimerRepository = TimerRepositoryImpl(),
    private val calculateProgressUseCase: CalculateTimerProgressUseCase = CalculateTimerProgressUseCase(),
    private val normalizeDurationUseCase: NormalizeDurationUseCase = NormalizeDurationUseCase(),
    private val timeSourceProvider: TimeSourceProvider = MonotonicTimeSourceProvider()
) : MviViewModel<TimerState, TimerIntent, TimerEffect>(
    TimerState(
        duration = timerRepository.getDefaultDuration(),
        totalDurationMillis = timerRepository.getDefaultDuration().totalMillis,
        remainingMillis = timerRepository.getDefaultDuration().totalMillis,
        availablePresets = timerRepository.getQuickPresetsSeconds()
    )
) {

    private var countdownJob: Job? = null
    private var startMark: TimeMark? = null
    private var baseRemainingMillis: Long = 60_000L

    /**
     * Central intent dispatcher: Processes all incoming user actions from the UI.
     */
    override fun handleIntent(intent: TimerIntent) {
        when (intent) {
            is TimerIntent.SetDuration -> setDuration(intent.hours, intent.minutes, intent.seconds)
            is TimerIntent.SetHours -> setDuration(intent.hours, currentState.duration.minutes, currentState.duration.seconds)
            is TimerIntent.SetMinutes -> setDuration(currentState.duration.hours, intent.minutes, currentState.duration.seconds)
            is TimerIntent.SetSeconds -> setDuration(currentState.duration.hours, currentState.duration.minutes, intent.seconds)
            is TimerIntent.AddSeconds -> addSeconds(intent.seconds)
            is TimerIntent.Start -> startTimer()
            is TimerIntent.Pause -> pauseTimer()
            is TimerIntent.Resume -> resumeTimer()
            is TimerIntent.Reset -> resetTimer()
            is TimerIntent.DismissFinished -> dismissFinished()
        }
    }

    private fun setDuration(hours: Int, minutes: Int, seconds: Int) {
        if (currentState.isRunning) return

        val normalized = normalizeDurationUseCase.fromHms(hours, minutes, seconds)
        val totalMs = normalized.totalMillis

        baseRemainingMillis = totalMs
        setState {
            copy(
                duration = normalized,
                totalDurationMillis = totalMs,
                remainingMillis = totalMs,
                progress = calculateProgressUseCase(totalMs, totalMs),
                isRunning = false,
                isPaused = false,
                isFinished = false
            )
        }
    }

    private fun addSeconds(seconds: Long) {
        val addMs = seconds * 1000L
        if (currentState.isRunning || currentState.isPaused) {
            baseRemainingMillis = (currentState.remainingMillis + addMs).coerceAtLeast(0L)
            val newTotal = (currentState.totalDurationMillis + addMs).coerceAtLeast(baseRemainingMillis)
            startMark = timeSourceProvider.markNow()
            setState {
                copy(
                    remainingMillis = baseRemainingMillis,
                    totalDurationMillis = newTotal,
                    progress = calculateProgressUseCase(baseRemainingMillis, newTotal),
                    isFinished = false
                )
            }
        } else {
            val updatedDuration = normalizeDurationUseCase.addSeconds(currentState.duration, seconds)
            setDuration(updatedDuration.hours, updatedDuration.minutes, updatedDuration.seconds)
        }
    }

    private fun startTimer() {
        if (currentState.isRunning) return

        val durationToStart = if (currentState.isPaused && currentState.remainingMillis > 0) {
            currentState.remainingMillis
        } else {
            currentState.duration.totalMillis
        }

        if (durationToStart <= 0L) return

        baseRemainingMillis = durationToStart
        startMark = timeSourceProvider.markNow()

        val totalDuration = if (currentState.isPaused) currentState.totalDurationMillis else durationToStart

        setState {
            copy(
                totalDurationMillis = totalDuration,
                remainingMillis = durationToStart,
                progress = calculateProgressUseCase(durationToStart, totalDuration),
                isRunning = true,
                isPaused = false,
                isFinished = false
            )
        }
        sendEffect(TimerEffect.Started)
        launchCountdownTicker()
    }

    private fun resumeTimer() {
        if (currentState.isRunning || currentState.remainingMillis <= 0L) return

        baseRemainingMillis = currentState.remainingMillis
        startMark = timeSourceProvider.markNow()

        setState { copy(isRunning = true, isPaused = false, isFinished = false) }
        sendEffect(TimerEffect.Started)
        launchCountdownTicker()
    }

    private fun pauseTimer() {
        if (!currentState.isRunning) return

        countdownJob?.cancel()
        countdownJob = null

        val mark = startMark
        if (mark != null) {
            val elapsedSinceStart = mark.elapsedNow().inWholeMilliseconds
            baseRemainingMillis = (baseRemainingMillis - elapsedSinceStart).coerceAtLeast(0L)
        }
        startMark = null

        setState {
            copy(
                remainingMillis = baseRemainingMillis,
                progress = calculateProgressUseCase(baseRemainingMillis, totalDurationMillis),
                isRunning = false,
                isPaused = true
            )
        }
        sendEffect(TimerEffect.Paused)
    }

    private fun resetTimer() {
        countdownJob?.cancel()
        countdownJob = null
        startMark = null

        val configuredMs = currentState.duration.totalMillis
        baseRemainingMillis = configuredMs

        setState {
            copy(
                remainingMillis = configuredMs,
                totalDurationMillis = configuredMs,
                progress = 1f,
                isRunning = false,
                isPaused = false,
                isFinished = false
            )
        }
        sendEffect(TimerEffect.ResetCompleted)
    }

    private fun dismissFinished() {
        setState { copy(isFinished = false) }
    }

    private fun launchCountdownTicker() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                val mark = startMark ?: break
                val elapsedSinceStart = mark.elapsedNow().inWholeMilliseconds
                val currentRemaining = (baseRemainingMillis - elapsedSinceStart).coerceAtLeast(0L)

                if (currentRemaining <= 0L) {
                    setState {
                        copy(
                            remainingMillis = 0L,
                            progress = 0f,
                            isRunning = false,
                            isPaused = false,
                            isFinished = true
                        )
                    }
                    sendEffect(TimerEffect.TimerFinished)
                    break
                } else {
                    val progress = calculateProgressUseCase(currentRemaining, currentState.totalDurationMillis)
                    setState {
                        copy(
                            remainingMillis = currentRemaining,
                            progress = progress
                        )
                    }
                }
                delay(30) // ~30fps smooth ring animation
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
