/**
 * id.my.hizari.moviy.ui.discover
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import id.my.hizari.moviy.R
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.ui.components.ErrorView
import id.my.hizari.moviy.ui.components.RatingBadge
import id.my.hizari.moviy.ui.components.ScrollShadowContainer
import id.my.hizari.moviy.ui.components.TestTags
import id.my.hizari.moviy.ui.components.UiText
import id.my.hizari.moviy.ui.components.shimmerEffect
import id.my.hizari.moviy.ui.theme.Dimens
import id.my.hizari.moviy.ui.theme.MoviyTheme

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    DiscoverContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent,
        onMovieClick = onMovieClick,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverContent(
    modifier: Modifier = Modifier,
    state: DiscoverState,
    onIntent: (DiscoverIntent) -> Unit,
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.genreName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.desc_back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
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
                    DiscoverShimmerGrid()
                }

                state.error != null -> {
                    ErrorView(
                        message = state.error.asString(),
                        onRetry = { onIntent(DiscoverIntent.Retry) }
                    )
                }

                else -> {
                    val gridState = rememberLazyGridState()
                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val lastVisibleItemIndex =
                                gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val totalItemsCount = gridState.layoutInfo.totalItemsCount
                            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 4
                        }
                    }

                    LaunchedEffect(shouldLoadMore.value) {
                        if (shouldLoadMore.value) {
                            onIntent(DiscoverIntent.LoadNextPage)
                        }
                    }

                    ScrollShadowContainer(
                        lazyGridState = gridState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(minSize = 130.dp),
                            contentPadding = PaddingValues(Dimens.PaddingMedium),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
                            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(TestTags.DISCOVER_GRID)
                        ) {
                            items(state.movies) { movie ->
                                MovieGridItem(
                                    movie = movie,
                                    onClick = { onMovieClick(movie.id) }
                                )
                            }

                            if (state.isLoadingNextPage) {
                                items(4) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(2f / 3f)
                                            .clip(RoundedCornerShape(Dimens.CornerMedium))
                                            .shimmerEffect()
                                    )
                                }
                            }

                            if (state.paginationError != null) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    PaginationErrorRow(
                                        message = state.paginationError.asString(),
                                        onRetry = { onIntent(DiscoverIntent.Retry) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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
        shape = RoundedCornerShape(Dimens.CornerMedium),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(Dimens.CornerMedium))
            .clickable(onClick = onClick)
            .border(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                shape = RoundedCornerShape(Dimens.CornerMedium)
            )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Rating Badge top-right
            RatingBadge(
                rating = movie.voteAverage,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.PaddingSmall)
            )

            // Title bottom overlay text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(Dimens.PaddingSmall)
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

@Composable
fun PaginationErrorRow(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(Dimens.CornerMedium),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.PaddingSmall)
            .border(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
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
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(Dimens.CornerNormal)
            ) {
                Text(
                    text = stringResource(R.string.btn_retry),
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun DiscoverShimmerGrid(
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 130.dp),
        contentPadding = PaddingValues(Dimens.PaddingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingNormal),
        modifier = modifier.fillMaxSize()
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(Dimens.CornerMedium))
                    .shimmerEffect()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverScreenSuccessPreview() {
    MoviyTheme {
        DiscoverContent(
            state = DiscoverState(
                genreName = "Action",
                movies = listOf(
                    Movie(1, "Test Movie 1", "Overview 1", null, null, null, 8.5),
                    Movie(2, "Test Movie 2", "Overview 2", null, null, null, 7.2)
                )
            ),
            onIntent = {},
            onMovieClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverScreenLoadingPreview() {
    MoviyTheme {
        DiscoverContent(
            state = DiscoverState(isLoading = true, genreName = "Action"),
            onIntent = {},
            onMovieClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverScreenErrorPreview() {
    MoviyTheme {
        DiscoverContent(
            state = DiscoverState(
                genreName = "Action",
                error = UiText.StringResource(R.string.error_connection_timeout)
            ),
            onIntent = {},
            onMovieClick = {},
            onBackClick = {}
        )
    }
}
