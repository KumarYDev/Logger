package com.asura.logger.presentation.stopwatch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asura.logger.presentation.stopwatch.components.StopwatchClockCard
import com.asura.logger.presentation.stopwatch.components.StopwatchControls
import com.asura.logger.presentation.stopwatch.components.StopwatchLapList

/**
 * ============================================================================
 * Presentation Screen: StopwatchScreen (MVI)
 * ============================================================================
 *
 * Observes [StopwatchState] from [StopwatchViewModel] and delegates user intents
 * via [StopwatchViewModel.sendIntent].
 */
@Composable
fun StopwatchScreen(
    viewModel: StopwatchViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    StopwatchContent(
        state = state,
        onIntent = viewModel::sendIntent,
        modifier = modifier
    )
}

/**
 * Pure, stateless Composable representation of the Stopwatch UI.
 *
 * Strictly accepts [StopwatchState] and an [onIntent] callback to satisfy MVI.
 */
@Composable
fun StopwatchContent(
    state: StopwatchState,
    onIntent: (StopwatchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Digital Clock
        StopwatchClockCard(
            elapsedMillis = state.elapsedMillis,
            currentLapDurationMillis = state.currentLapDurationMillis,
            hasLaps = state.laps.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Control Buttons (Lap, Start/Pause, Reset)
        StopwatchControls(
            isRunning = state.isRunning,
            canLap = state.canLap,
            canReset = state.canReset,
            onIntent = onIntent
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Recorded Laps Table
        if (state.laps.isNotEmpty()) {
            StopwatchLapList(
                laps = state.laps,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(80.dp)) // Clearance for floating navigation bar
    }
}
