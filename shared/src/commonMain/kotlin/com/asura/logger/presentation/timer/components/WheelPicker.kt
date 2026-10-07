package com.asura.logger.presentation.timer.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

/**
 * ============================================================================
 * Presentation Component: NumberWheelPicker
 * ============================================================================
 *
 * Smooth scrollable vertical wheel picker with snapping for selecting time values.
 * Isolated per-wheel state tracking prevents unwanted cascading updates.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberWheelPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp,
    visibleItemsCount: Int = 3
) {
    val items = remember(range) { range.toList() }
    val initialIndex = remember { items.indexOf(value).coerceAtLeast(0) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Current centered index derived from scroll offset
    val centeredIndex by remember {
        derivedStateOf {
            val firstVisible = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val threshold = itemHeight.value / 2f
            if (offset > threshold) {
                (firstVisible + 1).coerceIn(0, items.lastIndex)
            } else {
                firstVisible.coerceIn(0, items.lastIndex)
            }
        }
    }

    // Only notify parent when USER scroll finishes (drop(1) avoids initial false trigger)
    LaunchedEffect(listState) {
        var hadUserScroll = false
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    hadUserScroll = true
                } else if (hadUserScroll) {
                    hadUserScroll = false
                    val settledIndex = listState.firstVisibleItemIndex.coerceIn(0, items.lastIndex)
                    val selectedVal = items[settledIndex]
                    if (selectedVal != value) {
                        onValueChange(selectedVal)
                    }
                }
            }
    }

    // Sync external value changes (e.g., presets) without triggering loop
    LaunchedEffect(value) {
        if (!listState.isScrollInProgress) {
            val targetIndex = items.indexOf(value).coerceAtLeast(0)
            if (listState.firstVisibleItemIndex != targetIndex) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    val totalHeight = itemHeight * visibleItemsCount

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(84.dp)
                .height(totalHeight),
            contentAlignment = Alignment.Center
        ) {
            // Selected center highlight bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(14.dp)
                    )
            )

            // Scrollable column
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(vertical = itemHeight),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(items.size) { index ->
                    val itemVal = items[index]
                    val isSelected = index == centeredIndex
                    val distance = kotlin.math.abs(index - centeredIndex)

                    val alpha = when (distance) {
                        0 -> 1.0f
                        1 -> 0.4f
                        else -> 0.15f
                    }

                    val fontSize = when (distance) {
                        0 -> 30.sp
                        1 -> 20.sp
                        else -> 16.sp
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (itemVal < 10) "0$itemVal" else "$itemVal",
                            fontSize = fontSize,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.alpha(alpha),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
