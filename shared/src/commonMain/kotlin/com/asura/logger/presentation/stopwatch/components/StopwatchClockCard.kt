package com.asura.logger.presentation.stopwatch.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asura.logger.core.util.TimeFormatter

/**
 * ============================================================================
 * Presentation Component: StopwatchClockCard
 * ============================================================================
 *
 * Displays the main digital elapsed time and optional in-progress lap duration.
 */
@Composable
fun StopwatchClockCard(
    elapsedMillis: Long,
    currentLapDurationMillis: Long,
    hasLaps: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = TimeFormatter.formatStopwatch(elapsedMillis),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (hasLaps) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Lap: " + TimeFormatter.formatStopwatch(currentLapDurationMillis),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Preview
@Composable
private fun StopwatchClockCardPreview() {
    MaterialTheme {
        StopwatchClockCard(
            elapsedMillis = 65430L,
            currentLapDurationMillis = 15430L,
            hasLaps = true
        )
    }
}

@Preview
@Composable
private fun StopwatchClockCardInitialPreview() {
    MaterialTheme {
        StopwatchClockCard(
            elapsedMillis = 0L,
            currentLapDurationMillis = 0L,
            hasLaps = false
        )
    }
}
