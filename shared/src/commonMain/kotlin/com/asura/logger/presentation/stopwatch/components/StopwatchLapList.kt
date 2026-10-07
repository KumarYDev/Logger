package com.asura.logger.presentation.stopwatch.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asura.logger.core.util.TimeFormatter
import com.asura.logger.domain.model.Lap

/**
 * ============================================================================
 * Presentation Component: StopwatchLapList
 * ============================================================================
 *
 * Displays the scrollable table of laps with clean, uniform styling across all rows.
 * Automatically scrolls to reveal the latest recorded lap at the top of the list.
 */
@Composable
fun StopwatchLapList(
    laps: List<Lap>,
    modifier: Modifier = Modifier
) {
    if (laps.isEmpty()) return

    val listState = rememberLazyListState()

    // Automatically auto-scroll to the latest lap (index 0) whenever a new lap is added
    LaunchedEffect(laps.size) {
        if (laps.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Laps Recorded (${laps.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            textAlign = TextAlign.Start
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Lap #", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
            Text("Lap Time", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
            Text("Total Time", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
        }

        HorizontalDivider()

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(laps, key = { it.lapNumber }) { lap ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lap ${lap.lapNumber}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = TimeFormatter.formatStopwatch(lap.lapDurationMillis),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = TimeFormatter.formatStopwatch(lap.totalElapsedMillis),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StopwatchLapListPreview() {
    MaterialTheme {
        StopwatchLapList(
            laps = listOf(
                Lap(lapNumber = 3, lapDurationMillis = 15320L, totalElapsedMillis = 55320L),
                Lap(lapNumber = 2, lapDurationMillis = 20000L, totalElapsedMillis = 40000L),
                Lap(lapNumber = 1, lapDurationMillis = 20000L, totalElapsedMillis = 20000L)
            )
        )
    }
}
