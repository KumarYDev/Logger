package com.asura.logger.presentation.timer

import androidx.lifecycle.viewModelScope
import com.asura.logger.Log
import com.asura.logger.core.mvi.MviViewModel
import com.asura.logger.data.datasource.MonotonicTimeSourceProvider
import com.asura.logger.data.datasource.TimeSourceProvider
import com.asura.logger.data.repository.TimerRepositoryImpl
import com.asura.logger.domain.model.TimerDuration
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
        Log.d("Handling Timer intent: $intent", tag = "TimerViewModel_handleIntent()")
        when (intent) {
            is TimerIntent.SetDuration -> setDuration(intent.duration)
            is TimerIntent.AddSeconds -> addSeconds(intent.seconds)
            is TimerIntent.Start -> startTimer()
            is TimerIntent.Pause -> pauseTimer()
            is TimerIntent.Resume -> resumeTimer()
            is TimerIntent.Reset -> resetTimer()
            is TimerIntent.DismissFinished -> dismissFinished()
        }
    }

    private fun setDuration(duration: TimerDuration) {
        if (currentState.isRunning) {
            Log.d("setDuration ignored: timer is currently running", tag = "TimerViewModel_setDuration()")
            return
        }

        val normalized = normalizeDurationUseCase.fromHms(duration.hours, duration.minutes, duration.seconds)
        val totalMs = normalized.totalMillis

        Log.i("Configured timer duration set to: $normalized ($totalMs ms)", tag = "TimerViewModel_setDuration()")
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
        Log.i("Adding $seconds seconds ($addMs ms) to timer (isRunning=${currentState.isRunning}, isPaused=${currentState.isPaused})", tag = "TimerViewModel_addSeconds()")
        if (currentState.isRunning || currentState.isPaused) {
            baseRemainingMillis = (currentState.remainingMillis + addMs).coerceAtLeast(0L)
            val newTotal = (currentState.totalDurationMillis + addMs).coerceAtLeast(baseRemainingMillis)
            startMark = timeSourceProvider.markNow()
            Log.d("Live timer adjusted: baseRemainingMillis=$baseRemainingMillis, newTotalDuration=$newTotal", tag = "TimerViewModel_addSeconds()")
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
            setDuration(updatedDuration)
        }
    }

    private fun startTimer() {
        if (currentState.isRunning) {
            Log.d("startTimer ignored: timer is already running", tag = "TimerViewModel_startTimer()")
            return
        }

        val durationToStart = if (currentState.isPaused && currentState.remainingMillis > 0) {
            currentState.remainingMillis
        } else {
            currentState.duration.totalMillis
        }

        if (durationToStart <= 0L) {
            Log.d("startTimer ignored: durationToStart is $durationToStart ms (<= 0)", tag = "TimerViewModel_startTimer()")
            return
        }

        baseRemainingMillis = durationToStart
        startMark = timeSourceProvider.markNow()

        val totalDuration = if (currentState.isPaused) currentState.totalDurationMillis else durationToStart

        Log.i("Starting countdown timer: durationToStart=$durationToStart ms, totalDuration=$totalDuration ms", tag = "TimerViewModel_startTimer()")
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
        if (currentState.isRunning || currentState.remainingMillis <= 0L) {
            Log.d("resumeTimer ignored: isRunning=${currentState.isRunning}, remainingMillis=${currentState.remainingMillis}", tag = "TimerViewModel_resumeTimer()")
            return
        }

        baseRemainingMillis = currentState.remainingMillis
        startMark = timeSourceProvider.markNow()

        Log.i("Resuming countdown timer from remainingMillis=$baseRemainingMillis ms", tag = "TimerViewModel_resumeTimer()")
        setState { copy(isRunning = true, isPaused = false, isFinished = false) }
        sendEffect(TimerEffect.Started)
        launchCountdownTicker()
    }

    private fun pauseTimer() {
        if (!currentState.isRunning) {
            Log.d("pauseTimer ignored: timer is not running", tag = "TimerViewModel_pauseTimer()")
            return
        }

        countdownJob?.cancel()
        countdownJob = null

        val mark = startMark
        if (mark != null) {
            val elapsedSinceStart = mark.elapsedNow().inWholeMilliseconds
            baseRemainingMillis = (baseRemainingMillis - elapsedSinceStart).coerceAtLeast(0L)
            Log.i("Pausing timer at remainingMillis=$baseRemainingMillis ms (elapsed since start: $elapsedSinceStart ms)", tag = "TimerViewModel_pauseTimer()")
        } else {
            Log.i("Pausing timer with null mark at remainingMillis=$baseRemainingMillis ms", tag = "TimerViewModel_pauseTimer()")
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

        Log.i("Resetting timer to configured duration: $configuredMs ms", tag = "TimerViewModel_resetTimer()")
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
        Log.d("Dismissed timer finished state", tag = "TimerViewModel_dismissFinished()")
        setState { copy(isFinished = false) }
    }

    private fun launchCountdownTicker() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            Log.d("Countdown ticker coroutine started", tag = "TimerViewModel_launchCountdownTicker()")
            while (isActive) {
                val mark = startMark ?: break
                val elapsedSinceStart = mark.elapsedNow().inWholeMilliseconds
                val currentRemaining = (baseRemainingMillis - elapsedSinceStart).coerceAtLeast(0L)

                if (currentRemaining <= 0L) {
                    Log.i("Timer countdown finished (reached 0 ms)", tag = "TimerViewModel_launchCountdownTicker()")
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
            Log.d("Countdown ticker coroutine completed/cancelled", tag = "TimerViewModel_launchCountdownTicker()")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TimerViewModel onCleared called, canceling countdown job", tag = "TimerViewModel_onCleared()")
        countdownJob?.cancel()
    }
}
