package com.asura.logger.presentation.timer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asura.logger.Log
import com.asura.logger.presentation.timer.TimerIntent

/**
 * ============================================================================
 * Presentation Component: TimerPresetChips
 * ============================================================================
 *
 * Renders quick-add buttons (+30s, +1m, +5m, +10m) dispatching [TimerIntent.AddSeconds].
 */
@Composable
fun TimerPresetChips(
    presets: List<Long>,
    onIntent: (TimerIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        presets.forEach { seconds ->
            val label = when {
                seconds < 60 -> "+${seconds}s"
                else -> "+${seconds / 60}m"
            }
            FilledTonalButton(
                onClick = {
                    Log.d("Preset chip clicked: $label (+$seconds seconds)", tag = "TimerPresetChips_onClick()")
                    onIntent(TimerIntent.AddSeconds(seconds))
                },
                shape = RoundedCornerShape(12.dp),
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Text(text = label, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
        }
    }
}

@Preview
@Composable
private fun TimerPresetChipsPreview() {
    MaterialTheme {
        TimerPresetChips(
            presets = listOf(30L, 60L, 300L, 600L),
            onIntent = {}
        )
    }
}
