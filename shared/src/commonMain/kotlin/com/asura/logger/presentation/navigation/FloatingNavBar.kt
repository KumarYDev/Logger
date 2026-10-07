package com.asura.logger.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ============================================================================
 * Presentation Component: FloatingNavBar
 * ============================================================================
 *
 * Floating bottom navigation bar displaying ONLY icons/logos with an animated
 * pill selection indicator and drop shadow.
 */
@Composable
fun FloatingNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(bottom = 28.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(32.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stopwatch Logo Button
            FloatingNavIconButton(
                isSelected = currentScreen == AppScreen.STOPWATCH,
                onClick = { onNavigate(AppScreen.STOPWATCH) },
                icon = "⏱️"
            )

            // Timer / Hourglass Logo Button
            FloatingNavIconButton(
                isSelected = currentScreen == AppScreen.TIMER,
                onClick = { onNavigate(AppScreen.TIMER) },
                icon = "⏳"
            )
        }
    }
}

@Composable
private fun FloatingNavIconButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    val iconScale by animateDpAsState(
        targetValue = if (isSelected) 60.dp else 48.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    Box(
        modifier = modifier
            .width(iconScale)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = if (isSelected) 24.sp else 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun FloatingNavBarStopwatchPreview() {
    MaterialTheme {
        FloatingNavBar(
            currentScreen = AppScreen.STOPWATCH,
            onNavigate = {}
        )
    }
}

@Preview
@Composable
private fun FloatingNavBarTimerPreview() {
    MaterialTheme {
        FloatingNavBar(
            currentScreen = AppScreen.TIMER,
            onNavigate = {}
        )
    }
}
