package com.asura.logger.presentation.stopwatch.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asura.logger.presentation.stopwatch.StopwatchIntent

/**
 * ============================================================================
 * Presentation Component: StopwatchControls
 * ============================================================================
 *
 * Renders Lap, Start/Pause, and Reset buttons with spring physics and seamless
 * transitions between running, paused, and idle states without jerky layout jumps.
 */
@Composable
fun StopwatchControls(
    isRunning: Boolean,
    canLap: Boolean,
    canReset: Boolean,
    onIntent: (StopwatchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val showSecondaryButton = canLap || canReset

    val primaryButtonColor by animateColorAsState(
        targetValue = if (isRunning) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    val primaryButtonWidth by animateDpAsState(
        targetValue = if (showSecondaryButton) 130.dp else 160.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Smoothly animated Secondary Button (Lap when running, Reset when paused)
        AnimatedVisibility(
            visible = showSecondaryButton,
            enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    expandHorizontally(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                        expandFrom = Alignment.End
                    ),
            exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    shrinkHorizontally(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                        shrinkTowards = Alignment.End
                    )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = {
                        if (isRunning) {
                            onIntent(StopwatchIntent.LapRecorded)
                        } else {
                            onIntent(StopwatchIntent.Reset)
                        }
                    },
                    modifier = Modifier
                        .width(110.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    AnimatedContent(
                        targetState = if (isRunning) "Lap" else "Reset",
                        transitionSpec = {
                            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) togetherWith
                            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                        }
                    ) { label ->
                        Text(
                            text = label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        // Primary Action Button (Start / Pause / Resume)
        Button(
            onClick = {
                if (isRunning) {
                    onIntent(StopwatchIntent.Pause)
                } else {
                    onIntent(StopwatchIntent.Start)
                }
            },
            modifier = Modifier
                .width(primaryButtonWidth)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryButtonColor
            )
        ) {
            AnimatedContent(
                targetState = if (isRunning) "Pause" else if (canReset) "Resume" else "Start",
                transitionSpec = {
                    fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) togetherWith
                    fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                }
            ) { text ->
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}
