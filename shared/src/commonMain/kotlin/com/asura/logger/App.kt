package com.asura.logger

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asura.logger.presentation.navigation.AppScreen
import com.asura.logger.presentation.navigation.FloatingNavBar
import com.asura.logger.presentation.stopwatch.StopwatchScreen
import com.asura.logger.presentation.stopwatch.StopwatchViewModel
import com.asura.logger.presentation.timer.TimerScreen
import com.asura.logger.presentation.timer.TimerViewModel

/**
 * ============================================================================
 * Root Composable: App
 * ============================================================================
 *
 * Entry point for the Compose Multiplatform UI across Android, iOS, Desktop, and Web.
 * Connects the Presentation layer ViewModels and Floating Navigation Bar.
 */
@Composable
fun App() {
    MaterialTheme {
        // Navigation state between Stopwatch and Timer screens
        var currentScreen by remember { mutableStateOf(AppScreen.STOPWATCH) }

        // MVI ViewModels preserved during navigation transitions
        val stopwatchViewModel: StopwatchViewModel = viewModel { StopwatchViewModel() }
        val timerViewModel: TimerViewModel = viewModel { TimerViewModel() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
        ) {
            // Screen Content with smooth crossfade
            Crossfade(
                targetState = currentScreen,
                modifier = Modifier.fillMaxSize()
            ) { screen ->
                when (screen) {
                    AppScreen.STOPWATCH -> {
                        StopwatchScreen(viewModel = stopwatchViewModel)
                    }
                    AppScreen.TIMER -> {
                        TimerScreen(viewModel = timerViewModel)
                    }
                }
            }

            // Floating Navigation Bar (Logos only)
            FloatingNavBar(
                currentScreen = currentScreen,
                onNavigate = { screen -> currentScreen = screen },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}