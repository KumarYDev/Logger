package com.asura.logger.presentation.timer.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asura.logger.core.util.TimeFormatter

/**
 * ============================================================================
 * Presentation Component: TimerProgressRing
 * ============================================================================
 *
 * Renders the circular animated countdown gauge and time remaining label.
 */
@Composable
fun TimerProgressRing(
    remainingMillis: Long,
    totalConfiguredMillis: Long,
    progress: Float,
    isRunning: Boolean,
    isPaused: Boolean,
    isFinished: Boolean,
    isConfiguring: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(240.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background track
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 12.dp,
            strokeCap = StrokeCap.Round
        )

        // Dynamic progress track
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = if (isFinished) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            strokeWidth = 12.dp,
            strokeCap = StrokeCap.Round
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isFinished) {
                Text(
                    text = "Time's up!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "00:00",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                val displayMillis = if (isConfiguring) totalConfiguredMillis else remainingMillis
                Text(
                    text = TimeFormatter.formatTimer(displayMillis),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when {
                        isRunning -> "Running"
                        isPaused -> "Paused"
                        else -> "Ready"
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview
@Composable
private fun TimerProgressRingRunningPreview() {
    MaterialTheme {
        TimerProgressRing(
            remainingMillis = 145000L,
            totalConfiguredMillis = 300000L,
            progress = 0.48f,
            isRunning = true,
            isPaused = false,
            isFinished = false,
            isConfiguring = false
        )
    }
}

@Preview
@Composable
private fun TimerProgressRingFinishedPreview() {
    MaterialTheme {
        TimerProgressRing(
            remainingMillis = 0L,
            totalConfiguredMillis = 60000L,
            progress = 0f,
            isRunning = false,
            isPaused = false,
            isFinished = true,
            isConfiguring = false
        )
    }
}
