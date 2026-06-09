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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.my.hizari.moviy.R
import id.my.hizari.moviy.ui.theme.Dimens

@Composable
fun RatingBadge(
    modifier: Modifier = Modifier,
    rating: Double,
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
            contentDescription = stringResource(R.string.desc_rating_star),
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

@Preview
@Composable
fun RatingBadgePreview() {
    MaterialTheme {
        RatingBadge(rating = 8.5)
    }
}
