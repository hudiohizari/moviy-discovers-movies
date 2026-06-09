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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.ui.theme.Dimens

@Composable
fun MovieGridItem(
    modifier: Modifier = Modifier,
    movie: Movie,
    onClick: () -> Unit
) {
    val posterUrl = if (movie.posterPath != null) {
        "https://image.tmdb.org/t/p/w500${movie.posterPath}"
    } else {
        null
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(size = Dimens.CornerMedium),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 2f / 3f)
            .clip(shape = RoundedCornerShape(size = Dimens.CornerMedium))
            .clickable(onClick = onClick)
            .border(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                shape = RoundedCornerShape(size = Dimens.CornerMedium)
            )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(data = posterUrl)
                    .crossfade(enable = true)
                    .build(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Rating Badge top-right
            RatingBadge(
                rating = movie.voteAverage,
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .padding(all = Dimens.PaddingSmall)
            )

            // Title bottom overlay text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(all = Dimens.PaddingSmall)
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
