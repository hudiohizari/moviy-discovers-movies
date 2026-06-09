/**
 * id.my.hizari.moviy.ui.genres
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.genres

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.hizari.moviy.R
import id.my.hizari.moviy.domain.model.Genre
import id.my.hizari.moviy.ui.components.ErrorView
import id.my.hizari.moviy.ui.components.ScrollShadowContainer
import id.my.hizari.moviy.ui.components.TestTags
import id.my.hizari.moviy.ui.components.UiText
import id.my.hizari.moviy.ui.components.shimmerEffect
import id.my.hizari.moviy.ui.theme.Dimens
import id.my.hizari.moviy.ui.theme.MoviyTheme
import androidx.compose.ui.unit.dp

@Composable
fun GenreScreen(
    modifier: Modifier = Modifier,
    viewModel: GenreViewModel = hiltViewModel(),
    onGenreClick: (Int, String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    GenreContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent,
        onGenreClick = onGenreClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreContent(
    modifier: Modifier = Modifier,
    state: GenreState,
    onIntent: (GenreIntent) -> Unit,
    onGenreClick: (Int, String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_discover_genres),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },

        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    GenreShimmerGrid()
                }
                state.error != null -> {
                    ErrorView(
                        message = state.error.asString(),
                        onRetry = { onIntent(GenreIntent.Retry) }
                    )
                }
                else -> {
                    GenreGrid(
                        genres = state.genres,
                        onGenreClick = onGenreClick
                    )
                }
            }
        }
    }
}

@Composable
fun GenreGrid(
    modifier: Modifier = Modifier,
    genres: List<Genre>,
    onGenreClick: (Int, String) -> Unit,
) {
    val gridState = rememberLazyGridState()
    ScrollShadowContainer(
        lazyGridState = gridState,
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
            modifier = Modifier
                .fillMaxSize()
                .testTag(TestTags.GENRE_GRID)
        ) {
            items(genres) { genre ->
                GenreCard(
                    genre = genre,
                    onClick = { onGenreClick(genre.id, genre.name) }
                )
            }
        }
    }
}

@Composable
fun GenreCard(
    modifier: Modifier = Modifier,
    genre: Genre,
    onClick: () -> Unit,
) {
    val cardGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Dimens.CornerMedium),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.IconLarge * 2f)
            .clip(RoundedCornerShape(Dimens.CornerMedium))
            .clickable(onClick = onClick)
            .border(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                shape = RoundedCornerShape(Dimens.CornerMedium)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = cardGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(Dimens.PaddingMedium)
            )
        }
    }
}

@Composable
fun GenreShimmerGrid(
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(Dimens.PaddingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
        modifier = modifier
            .fillMaxSize()
            .testTag(TestTags.LOADING_SHIMMER)
    ) {
        items(10) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.IconLarge * 2f)
                    .clip(RoundedCornerShape(Dimens.CornerMedium))
                    .shimmerEffect()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GenreScreenSuccessPreview() {
    MoviyTheme {
        GenreContent(
            state = GenreState(
                genres = listOf(
                    Genre(1, "Action"),
                    Genre(2, "Comedy"),
                    Genre(3, "Drama"),
                    Genre(4, "Horror")
                )
            ),
            onIntent = {},
            onGenreClick = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenreScreenLoadingPreview() {
    MoviyTheme {
        GenreContent(
            state = GenreState(isLoading = true),
            onIntent = {},
            onGenreClick = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenreScreenErrorPreview() {
    MoviyTheme {
        GenreContent(
            state = GenreState(error = UiText.StringResource(R.string.error_connection_timeout)),
            onIntent = {},
            onGenreClick = { _, _ -> }
        )
    }
}
