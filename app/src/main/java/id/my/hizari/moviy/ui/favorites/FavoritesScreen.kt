/**
 * id.my.hizari.moviy.ui.favorites
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.hizari.moviy.R
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.ui.components.ErrorView
import id.my.hizari.moviy.ui.components.MovieGridItem
import id.my.hizari.moviy.ui.components.ScrollShadowContainer
import id.my.hizari.moviy.ui.components.TestTags
import id.my.hizari.moviy.ui.components.UiText
import id.my.hizari.moviy.ui.components.shimmerEffect
import id.my.hizari.moviy.ui.theme.Dimens
import id.my.hizari.moviy.ui.theme.MoviyTheme

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    FavoritesContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent,
        onMovieClick = onMovieClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    modifier: Modifier = Modifier,
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit,
    onMovieClick: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.nav_favorites),
                        style = MaterialTheme.typography.titleLarge,
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
                .padding(paddingValues = innerPadding)
        ) {
            when {
                state.isLoading -> {
                    FavoritesShimmerGrid()
                }

                state.error != null -> {
                    ErrorView(
                        message = state.error.asString(),
                        onRetry = { onIntent(FavoritesIntent.LoadFavorites) }
                    )
                }

                state.movies.isEmpty() -> {
                    FavoritesEmptyState()
                }

                else -> {
                    val gridState = rememberLazyGridState()
                    ScrollShadowContainer(
                        lazyGridState = gridState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(minSize = Dimens.GridItemMinWidthMovie),
                            contentPadding = PaddingValues(all = Dimens.PaddingMedium),
                            horizontalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
                            verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(tag = TestTags.FAVORITES_GRID)
                        ) {
                            items(
                                items = state.movies,
                                key = { it.id }
                            ) { movie ->
                                MovieGridItem(
                                    movie = movie,
                                    onClick = { onMovieClick(movie.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesEmptyState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.height(height = Dimens.IconLarge * 2)
        )
        Spacer(modifier = Modifier.height(height = Dimens.PaddingNormal))
        Text(
            text = stringResource(id = R.string.empty_favorites_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(height = Dimens.PaddingSmall))
        Text(
            text = stringResource(id = R.string.empty_favorites_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FavoritesShimmerGrid(
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = Dimens.GridItemMinWidthMovie),
        contentPadding = PaddingValues(all = Dimens.PaddingMedium),
        horizontalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
        verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
        modifier = modifier
            .fillMaxSize()
            .testTag(tag = TestTags.LOADING_SHIMMER)
    ) {
        items(count = 6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 2f / 3f)
                    .clip(shape = RoundedCornerShape(size = Dimens.CornerMedium))
                    .shimmerEffect()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenSuccessPreview() {
    MoviyTheme {
        FavoritesContent(
            state = FavoritesState(
                movies = listOf(
                    Movie(1, "Favorite Movie 1", "Overview 1", null, null, null, 8.5),
                    Movie(2, "Favorite Movie 2", "Overview 2", null, null, null, 7.2)
                )
            ),
            onIntent = {},
            onMovieClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenLoadingPreview() {
    MoviyTheme {
        FavoritesContent(
            state = FavoritesState(isLoading = true),
            onIntent = {},
            onMovieClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenEmptyPreview() {
    MoviyTheme {
        FavoritesContent(
            state = FavoritesState(),
            onIntent = {},
            onMovieClick = {}
        )
    }
}
