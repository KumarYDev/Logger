package com.asura.logger.presentation.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.asura.logger.Log
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
 *
 * Fully responsive across standard phones, foldable devices (folded & unfolded),
 * tablets, and desktop/web viewports.
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
 *
 * Automatically adapts between Compact Single-Column layout (folded screens & phone portrait)
 * and Dual-Pane Side-by-Side layout (unfolded foldables, landscape, tablets, desktop).
 */
@Composable
fun TimerContent(
    state: TimerState,
    onIntent: (TimerIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 600.dp || (maxWidth > maxHeight && maxWidth >= 480.dp)

        if (isExpanded) {
            TimerExpandedContent(
                state = state,
                onIntent = onIntent
            )
        } else {
            TimerCompactContent(
                state = state,
                onIntent = onIntent
            )
        }
    }
}

/**
 * Compact Layout: Single vertical scrollable column.
 * Used on phone portrait and folded outer screens.
 */
@Composable
private fun TimerCompactContent(
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
                    FilledTonalButton(onClick = {
                        Log.d("Dismiss button clicked on timer finished alert", tag = "TimerScreen_onClick()")
                        onIntent(TimerIntent.DismissFinished)
                    }) {
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
                    TimerDurationPicker(
                        duration = state.duration,
                        onIntent = onIntent
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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
                    TimerProgressRing(
                        remainingMillis = state.remainingMillis,
                        totalConfiguredMillis = state.totalDurationMillis,
                        progress = state.progress,
                        isRunning = state.isRunning,
                        isPaused = state.isPaused,
                        isFinished = state.isFinished,
                        isConfiguring = false,
                        modifier = Modifier.size(240.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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

/**
 * Expanded Layout: Side-by-side dual-pane layout.
 * Used on unfolded foldable devices (square/wide aspect ratio), landscape phones, and tablets.
 */
@Composable
private fun TimerExpandedContent(
    state: TimerState,
    onIntent: (TimerIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Pane: Duration Picker (Configuring) or Progress Ring (Running/Paused/Finished)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = state.isConfiguring) { isConfiguring ->
                if (isConfiguring) {
                    TimerDurationPicker(
                        duration = state.duration,
                        onIntent = onIntent
                    )
                } else {
                    TimerProgressRing(
                        remainingMillis = state.remainingMillis,
                        totalConfiguredMillis = state.totalDurationMillis,
                        progress = state.progress,
                        isRunning = state.isRunning,
                        isPaused = state.isPaused,
                        isFinished = state.isFinished,
                        isConfiguring = false,
                        modifier = Modifier.size(220.dp)
                    )
                }
            }
        }

        // Right Pane: Presets + Controls + Finished Alert
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔔 Timer Finished!",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        FilledTonalButton(onClick = {
                            Log.d("Dismiss button clicked on expanded timer finished alert", tag = "TimerScreen_onClick()")
                            onIntent(TimerIntent.DismissFinished)
                        }) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            Text(
                text = "Quick Presets",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Presets
            TimerPresetChips(
                presets = state.availablePresets,
                onIntent = onIntent
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Controls
            TimerControls(
                isRunning = state.isRunning,
                isPaused = state.isPaused,
                isFinished = state.isFinished,
                canStart = state.canStart,
                onIntent = onIntent
            )
        }
    }
}

@Preview
@Composable
private fun TimerContentCompactConfiguringPreview() {
    MaterialTheme {
        TimerContent(
            state = TimerState(
                duration = TimerDuration(hours = 0, minutes = 5, seconds = 0),
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
private fun TimerContentCompactRunningPreview() {
    MaterialTheme {
        TimerContent(
            state = TimerState(
                duration = TimerDuration(hours = 0, minutes = 5, seconds = 0),
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

@Preview(widthDp = 700, heightDp = 750) // Foldable Unfolded Inner Screen
@Composable
private fun TimerContentFoldableUnfoldedPreview() {
    MaterialTheme {
        TimerContent(
            state = TimerState(
                duration = TimerDuration(hours = 0, minutes = 10, seconds = 0),
                totalDurationMillis = 600_000L,
                remainingMillis = 420_000L,
                isRunning = true,
                isPaused = false,
                isFinished = false,
                progress = 0.7f
            ),
            onIntent = {}
        )
    }
}

@Preview(widthDp = 800, heightDp = 400) // Phone Landscape
@Composable
private fun TimerContentLandscapeRunningPreview() {
    MaterialTheme {
        TimerContent(
            state = TimerState(
                duration = TimerDuration(hours = 0, minutes = 5, seconds = 0),
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
