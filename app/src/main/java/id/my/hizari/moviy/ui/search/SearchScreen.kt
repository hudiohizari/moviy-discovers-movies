/**
 * id.my.hizari.moviy.ui.search
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.hizari.moviy.R
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.ui.components.ErrorView
import id.my.hizari.moviy.ui.components.ScrollShadowContainer
import id.my.hizari.moviy.ui.components.TestTags
import id.my.hizari.moviy.ui.components.UiText
import id.my.hizari.moviy.ui.components.shimmerEffect
import id.my.hizari.moviy.ui.discover.DiscoverShimmerGrid
import id.my.hizari.moviy.ui.discover.MovieGridItem
import id.my.hizari.moviy.ui.discover.PaginationErrorRow
import id.my.hizari.moviy.ui.theme.Dimens
import id.my.hizari.moviy.ui.theme.MoviyTheme

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    SearchContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent,
        onMovieClick = onMovieClick,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.hint_search_movies),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(size = Dimens.CornerMedium),
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { onIntent(SearchIntent.QueryChanged(query = "")) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(id = R.string.desc_clear_query),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = Dimens.PaddingSmall)
                            .testTag(TestTags.SEARCH_INPUT)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.desc_back),
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
                .padding(paddingValues = innerPadding)
        ) {
            when {
                state.isLoading -> {
                    DiscoverShimmerGrid()
                }

                state.error != null -> {
                    ErrorView(
                        message = state.error.asString(),
                        onRetry = { onIntent(SearchIntent.Retry) }
                    )
                }

                state.query.trim().isEmpty() -> {
                    SearchEmptyState(
                        message = stringResource(id = R.string.search_start_typing)
                    )
                }

                state.movies.isEmpty() -> {
                    SearchEmptyState(
                        message = stringResource(id = R.string.search_no_results, state.query)
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

                    LaunchedEffect(key1 = shouldLoadMore.value) {
                        if (shouldLoadMore.value) {
                            onIntent(SearchIntent.LoadNextPage)
                        }
                    }

                    ScrollShadowContainer(
                        lazyGridState = gridState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(minSize = 130.dp),
                            contentPadding = PaddingValues(all = Dimens.PaddingMedium),
                            horizontalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
                            verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
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
                                items(count = 4) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(2f / 3f)
                                            .clip(shape = RoundedCornerShape(size = Dimens.CornerMedium))
                                            .shimmerEffect()
                                    )
                                }
                            }

                            if (state.paginationError != null) {
                                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                                    PaginationErrorRow(
                                        message = state.paginationError.asString(),
                                        onRetry = { onIntent(SearchIntent.Retry) }
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
fun SearchEmptyState(
    modifier: Modifier = Modifier,
    message: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier.size(Dimens.IconExtraLarge)
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenStartSearchingPreview() {
    MoviyTheme {
        SearchContent(
            state = SearchState(query = ""),
            onIntent = {},
            onMovieClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenResultsPreview() {
    MoviyTheme {
        SearchContent(
            state = SearchState(
                query = "Mandalorian",
                movies = listOf(
                    Movie(
                        id = 1,
                        title = "The Mandalorian",
                        overview = "Overview description here",
                        posterPath = null,
                        backdropPath = null,
                        releaseDate = null,
                        voteAverage = 8.5
                    )
                )
            ),
            onIntent = {},
            onMovieClick = {},
            onBackClick = {}
        )
    }
}
