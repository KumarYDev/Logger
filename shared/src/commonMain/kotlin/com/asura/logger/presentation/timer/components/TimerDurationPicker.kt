package com.asura.logger.presentation.timer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asura.logger.domain.model.TimerDuration
import com.asura.logger.presentation.timer.TimerIntent

/**
 * ============================================================================
 * Presentation Component: TimerDurationPicker
 * ============================================================================
 *
 * Provides a vertical wheel picker for Hours, Minutes, and Seconds.
 * Users can scroll up and down to select values. Emits [TimerIntent.SetDuration].
 */
@Composable
fun TimerDurationPicker(
    duration: TimerDuration,
    onIntent: (TimerIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Timer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hours Wheel (0..23)
                NumberWheelPicker(
                    value = duration.hours,
                    range = 0..23,
                    label = "hours",
                    onValueChange = { newHours ->
                        onIntent(TimerIntent.SetDuration(newHours, duration.minutes, duration.seconds))
                    }
                )

                Text(
                    text = ":",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Minutes Wheel (0..59)
                NumberWheelPicker(
                    value = duration.minutes,
                    range = 0..59,
                    label = "min",
                    onValueChange = { newMinutes ->
                        onIntent(TimerIntent.SetDuration(duration.hours, newMinutes, duration.seconds))
                    }
                )

                Text(
                    text = ":",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Seconds Wheel (0..59)
                NumberWheelPicker(
                    value = duration.seconds,
                    range = 0..59,
                    label = "sec",
                    onValueChange = { newSeconds ->
                        onIntent(TimerIntent.SetDuration(duration.hours, duration.minutes, newSeconds))
                    }
                )
            }
        }
    }
}
