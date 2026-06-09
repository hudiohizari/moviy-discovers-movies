/**
 * id.my.hizari.moviy.ui.components
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.components

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import id.my.hizari.moviy.R
import id.my.hizari.moviy.ui.theme.Dimens
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

@Composable
fun ApiKeyMissingScreen(
    modifier: Modifier = Modifier,
) {
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
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
            .padding(Dimens.PaddingMedium)
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
                modifier = Modifier.padding(Dimens.PaddingMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        contentDescription = stringResource(R.string.desc_missing_api_key),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(Dimens.IconMedium)
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                Text(
                    text = stringResource(R.string.title_api_key_required),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                Text(
                    text = stringResource(R.string.desc_api_key_instructions),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                TutorialStepRow(
                    stepNumber = "1",
                    text = stringResource(R.string.step_open_properties)
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingNormal))

                TutorialStepRow(
                    stepNumber = "2",
                    text = stringResource(R.string.step_paste_snippet)
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingNormal))

                TutorialStepRow(
                    stepNumber = "3",
                    text = stringResource(R.string.step_replace_placeholder)
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

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
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
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
                                contentDescription = stringResource(R.string.desc_copy_to_clipboard),
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

@Composable
fun TutorialStepRow(
    modifier: Modifier = Modifier,
    stepNumber: String,
    text: String,
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

@Preview(showBackground = true)
@Composable
fun ApiKeyMissingScreenPreview() {
    MaterialTheme {
        ApiKeyMissingScreen()
    }
}
