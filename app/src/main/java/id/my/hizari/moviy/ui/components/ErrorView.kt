/**
 * id.my.hizari.moviy.ui.components
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import id.my.hizari.moviy.R
import id.my.hizari.moviy.ui.theme.Dimens
import id.my.hizari.moviy.ui.theme.MoviyTheme

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit = {}
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
                        contentDescription = stringResource(R.string.desc_warning),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(Dimens.IconMedium)
                    )
                }
                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                Text(
                    text = stringResource(R.string.error_something_went_wrong),
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
                        text = stringResource(R.string.btn_retry_connection),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    MoviyTheme {
        ErrorView(message = stringResource(R.string.preview_error_network)) {}
    }
}
