package com.asura.logger.presentation.stopwatch

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asura.logger.domain.model.Lap
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
 *
 * Fully responsive across standard phones, foldable devices (folded & unfolded),
 * tablets, and desktop/web viewports.
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
 * Automatically adapts between Compact Single-Column layout (folded screens & phone portrait)
 * and Dual-Pane Side-by-Side layout (unfolded foldables, landscape, tablets, desktop).
 */
@Composable
fun StopwatchContent(
    state: StopwatchState,
    onIntent: (StopwatchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 600.dp || (maxWidth > maxHeight && maxWidth >= 480.dp)

        if (isExpanded) {
            StopwatchExpandedContent(
                state = state,
                onIntent = onIntent
            )
        } else {
            StopwatchCompactContent(
                state = state,
                onIntent = onIntent
            )
        }
    }
}

/**
 * Compact Layout: Vertically stacked Clock -> Controls -> Lap Table.
 * Used on phone portrait and folded outer screens.
 */
@Composable
private fun StopwatchCompactContent(
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

/**
 * Expanded Layout: Side-by-side dual-pane layout.
 * Used on unfolded foldable devices (square/wide aspect ratio), landscape phones, and tablets.
 */
@Composable
private fun StopwatchExpandedContent(
    state: StopwatchState,
    onIntent: (StopwatchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Pane: Clock Card + Controls
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StopwatchClockCard(
                elapsedMillis = state.elapsedMillis,
                currentLapDurationMillis = state.currentLapDurationMillis,
                hasLaps = state.laps.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(24.dp))

            StopwatchControls(
                isRunning = state.isRunning,
                canLap = state.canLap,
                canReset = state.canReset,
                onIntent = onIntent
            )

            Spacer(modifier = Modifier.height(50.dp)) // Clearance for bottom navigation bar
        }

        // Right Pane: Lap List or Empty State Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.laps.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    StopwatchLapList(
                        laps = state.laps,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "⏱️",
                                fontSize = 36.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Recorded laps will appear here",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StopwatchContentCompactIdlePreview() {
    MaterialTheme {
        StopwatchContent(
            state = StopwatchState(isRunning = false, elapsedMillis = 0L, laps = emptyList()),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun StopwatchContentCompactRunningPreview() {
    MaterialTheme {
        StopwatchContent(
            state = StopwatchState(
                isRunning = true,
                elapsedMillis = 75430L,
                laps = listOf(
                    Lap(lapNumber = 2, lapDurationMillis = 35000L, totalElapsedMillis = 75430L),
                    Lap(lapNumber = 1, lapDurationMillis = 40430L, totalElapsedMillis = 40430L)
                )
            ),
            onIntent = {}
        )
    }
}

@Preview(widthDp = 700, heightDp = 750) // Foldable Unfolded Inner Screen
@Composable
private fun StopwatchContentFoldableUnfoldedPreview() {
    MaterialTheme {
        StopwatchContent(
            state = StopwatchState(
                isRunning = true,
                elapsedMillis = 115430L,
                laps = listOf(
                    Lap(lapNumber = 3, lapDurationMillis = 40000L, totalElapsedMillis = 115430L),
                    Lap(lapNumber = 2, lapDurationMillis = 35000L, totalElapsedMillis = 75430L),
                    Lap(lapNumber = 1, lapDurationMillis = 40430L, totalElapsedMillis = 40430L)
                )
            ),
            onIntent = {}
        )
    }
}

@Preview(widthDp = 800, heightDp = 400) // Phone Landscape
@Composable
private fun StopwatchContentLandscapePreview() {
    MaterialTheme {
        StopwatchContent(
            state = StopwatchState(
                isRunning = true,
                elapsedMillis = 95430L,
                laps = listOf(
                    Lap(lapNumber = 2, lapDurationMillis = 35000L, totalElapsedMillis = 75430L),
                    Lap(lapNumber = 1, lapDurationMillis = 40430L, totalElapsedMillis = 40430L)
                )
            ),
            onIntent = {}
        )
    }
}
