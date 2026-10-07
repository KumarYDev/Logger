package com.asura.logger.presentation.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asura.logger.domain.model.TimerDuration
import com.asura.logger.presentation.timer.components.TimerControls
import com.asura.logger.presentation.timer.components.TimerDurationPicker
import com.asura.logger.presentation.timer.components.TimerPresetChips
import com.asura.logger.presentation.timer.components.TimerProgressRing

/**
 * ============================================================================
 * Presentation Screen: TimerScreen (MVI)
 * ============================================================================
 *
 * In Configuration mode: Displays the scrollable Wheel Picker to select duration.
 * When Started: Hides the wheel picker and reveals the animated Countdown Progress
 * Ring and remaining time display.
 */
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    TimerContent(
        state = state,
        onIntent = viewModel::sendIntent,
        modifier = modifier
    )
}

/**
 * Stateless Composable UI for Timer.
 */
@Composable
fun TimerContent(
    state: TimerState,
    onIntent: (TimerIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Finished Alert Banner
        AnimatedVisibility(visible = state.isFinished) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔔 Timer Finished!",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    FilledTonalButton(onClick = { onIntent(TimerIntent.DismissFinished) }) {
                        Text("Dismiss")
                    }
                }
            }
        }

        // Crossfade between Configuration Wheel Picker & Active Countdown Ring
        Crossfade(targetState = state.isConfiguring) { isConfiguring ->
            if (isConfiguring) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Scrollable Wheel Picker for Hours, Minutes, Seconds
                    TimerDurationPicker(
                        duration = state.duration,
                        onIntent = onIntent
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick Preset Chips (+30s, +1m, +5m, +10m)
                    TimerPresetChips(
                        presets = state.availablePresets,
                        onIntent = onIntent
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Countdown Ring & Active Remaining Time Display
                    TimerProgressRing(
                        remainingMillis = state.remainingMillis,
                        totalConfiguredMillis = state.totalDurationMillis,
                        progress = state.progress,
                        isRunning = state.isRunning,
                        isPaused = state.isPaused,
                        isFinished = state.isFinished,
                        isConfiguring = false
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Add Preset Chips while running/paused
                    TimerPresetChips(
                        presets = state.availablePresets,
                        onIntent = onIntent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Control Buttons (Start / Pause / Resume, Reset)
        TimerControls(
            isRunning = state.isRunning,
            isPaused = state.isPaused,
            isFinished = state.isFinished,
            canStart = state.canStart,
            onIntent = onIntent
        )

        Spacer(modifier = Modifier.height(100.dp)) // Clearance for floating navigation
    }
}

@Preview
@Composable
private fun TimerContentConfiguringPreview() {
    MaterialTheme {
        TimerContent(
            state = TimerState(
                duration = com.asura.logger.domain.model.TimerDuration(hours = 0, minutes = 5, seconds = 0),
                totalDurationMillis = 300_000L,
                remainingMillis = 300_000L,
                isRunning = false,
                isPaused = false,
                isFinished = false,
                progress = 1f
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun TimerContentRunningPreview() {
    MaterialTheme {
        TimerContent(
            state = TimerState(
                duration = com.asura.logger.domain.model.TimerDuration(hours = 0, minutes = 5, seconds = 0),
                totalDurationMillis = 300_000L,
                remainingMillis = 180_000L,
                isRunning = true,
                isPaused = false,
                isFinished = false,
                progress = 0.6f
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun TimerContentFinishedPreview() {
    MaterialTheme {
        TimerContent(
            state = TimerState(
                duration = com.asura.logger.domain.model.TimerDuration(hours = 0, minutes = 1, seconds = 0),
                totalDurationMillis = 60_000L,
                remainingMillis = 0L,
                isRunning = false,
                isPaused = false,
                isFinished = true,
                progress = 0f
            ),
            onIntent = {}
        )
    }
}
