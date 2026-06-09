/**
 * id.my.hizari.moviy.ui.components
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScrollShadowContainer(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    topShadowHeight: Dp = 8.dp,
    bottomShadowHeight: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val showTopShadow by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex > 0 || lazyGridState.firstVisibleItemScrollOffset > 0
        }
    }
    val showBottomShadow by remember {
        derivedStateOf {
            lazyGridState.canScrollForward
        }
    }

    Box(modifier = modifier) {
        content()

        // Top Shadow
        if (showTopShadow) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topShadowHeight)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Bottom Shadow
        if (showBottomShadow) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomShadowHeight)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                            )
                        )
                    )
            )
        }
    }
}
