/**
 * id.my.hizari.moviy.ui.components
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.components

import android.content.ClipData
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import id.my.hizari.moviy.ui.theme.Dimens

// Shimmer effect modifier for screen loading states
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.surface
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 300f, y = translateAnim - 300f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    this.background(brush = brush)
}

// Premium Error Card utilizing a frosted glass layout and styled retry button
@Composable
fun ErrorView(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.PaddingLarge)
            .testTag(TestTags.ERROR_CARD),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(Dimens.CornerLarge),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = Dimens.BorderThin,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(Dimens.CornerLarge)
                )
        ) {
            Column(
                modifier = Modifier.padding(Dimens.PaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.IconExtraLarge)
                        .background(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .border(
                            width = Dimens.BorderThin,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(Dimens.IconMedium)
                    )
                }
                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                Text(
                    text = "Something went wrong",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(Dimens.CornerMedium),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.ButtonHeightNormal)
                        .testTag(TestTags.RETRY_BUTTON)
                ) {
                    Text(
                        text = "Retry Connection",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

// Compact rating badge component to display movie vote averages with a subtle glow border
@Composable
fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(Dimens.CornerNormal)
            )
            .border(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(Dimens.CornerNormal)
            )
            .padding(horizontal = Dimens.PaddingSmall, vertical = Dimens.PaddingTiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating Star",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(Dimens.IconSmall)
        )
        Spacer(modifier = Modifier.width(Dimens.PaddingTiny))
        Text(
            text = String.format("%.1f", rating),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

// Premium Configuration tutorial screen shown when the TMDB API key is missing
@Composable
fun ApiKeyMissingScreen() {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val propertiesCodeSnippet = "TMDB_API_KEY=your_api_key_here"

    val backgroundBrush = Brush.radialGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        ),
        radius = 1200f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
            .padding(Dimens.PaddingMedium) // Reduced outer padding slightly for better content fit
            .testTag(TestTags.API_KEY_MISSING_SCREEN),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(Dimens.CornerLarge),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = Dimens.BorderThin,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(Dimens.CornerLarge)
                )
        ) {
            Column(
                modifier = Modifier.padding(Dimens.PaddingMedium), // Changed inner padding to PaddingMedium to prevent horizontal clipping
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glow Circle warning icon header
                Box(
                    modifier = Modifier
                        .size(Dimens.IconExtraLarge)
                        .background(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                            shape = CircleShape
                        )
                        .border(
                            width = Dimens.BorderThin,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Missing API Key",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(Dimens.IconMedium)
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                Text(
                    text = "TMDB API Key Required",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                Text(
                    text = "To run this application, you must add your The Movie Database (TMDB) API key (v3) to local.properties.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // Step 1 Layout Row
                TutorialStepRow(
                    stepNumber = "1",
                    text = "Open 'local.properties' file located in the root of the project."
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingNormal))

                // Step 2 Layout Row
                TutorialStepRow(
                    stepNumber = "2",
                    text = "Paste the TMDB API key parameter code snippet."
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingNormal))

                // Step 3 Layout Row
                TutorialStepRow(
                    stepNumber = "3",
                    text = "Replace the placeholder and run/build the project again."
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // Terminal Shell Container for Code Snippet
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(Dimens.CornerMedium),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = Dimens.BorderThin,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(Dimens.CornerMedium)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.PaddingNormal),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = propertiesCodeSnippet,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f) // Added weight to allow text wrapping/scaling and prevent horizontal overflow
                        )
                        Spacer(modifier = Modifier.width(Dimens.PaddingSmall)) // Breathing spacer before the copy button
                        IconButton(
                            onClick = {
                                scope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "TMDB_API_KEY",
                                                propertiesCodeSnippet
                                            )
                                        )
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(Dimens.IconButtonSmall)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy to clipboard",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(Dimens.IconSmall)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Styled Step component with gradient index badge
@Composable
fun TutorialStepRow(
    stepNumber: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        val numberGradient = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary
            )
        )
        Box(
            modifier = Modifier
                .size(Dimens.StepCircleSize)
                .background(brush = numberGradient, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Previews for Visual Inspection in IDE
@Preview(showBackground = true)
@Composable
fun ApiKeyMissingScreenPreview() {
    MaterialTheme {
        ApiKeyMissingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    MaterialTheme {
        ErrorView(message = "Network error: Connection timeout.") {}
    }
}

@Preview
@Composable
fun RatingBadgePreview() {
    MaterialTheme {
        RatingBadge(rating = 8.5)
    }
}
