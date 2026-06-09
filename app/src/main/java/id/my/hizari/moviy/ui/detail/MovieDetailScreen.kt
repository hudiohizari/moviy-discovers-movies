/**
 * id.my.hizari.moviy.ui.detail
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import id.my.hizari.moviy.R
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.model.Review
import id.my.hizari.moviy.ui.components.ErrorView
import id.my.hizari.moviy.ui.components.RatingBadge
import id.my.hizari.moviy.ui.components.TestTags
import id.my.hizari.moviy.ui.components.shimmerEffect
import id.my.hizari.moviy.ui.theme.Dimens

@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    MovieDetailContent(
        modifier = modifier,
        state = state,
        onIntent = { intent -> viewModel.handleIntent(intent = intent) },
        onBackClick = onBackClick,
    )
}

@Composable
fun MovieDetailContent(
    modifier: Modifier = Modifier,
    state: MovieDetailState,
    onIntent: (MovieDetailIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            when {
                state.isLoadingDetails && state.movie == null -> {
                    DetailShimmer(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(tag = TestTags.LOADING_SHIMMER)
                    )
                }

                state.errorDetails != null && state.movie == null -> {
                    ErrorView(
                        message = state.errorDetails.asString(),
                        onRetry = { onIntent(MovieDetailIntent.RetryDetails) }
                    )
                }

                else -> {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val isTablet = maxWidth >= 600.dp
                        if (isTablet) {
                            ExpandedDetailLayout(
                                state = state,
                                onIntent = onIntent,
                                onBackClick = onBackClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            CompactDetailLayout(
                                state = state,
                                onIntent = onIntent,
                                onBackClick = onBackClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactDetailLayout(
    modifier: Modifier = Modifier,
    state: MovieDetailState,
    onIntent: (MovieDetailIntent) -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 2
        }
    }

    LaunchedEffect(key1 = shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onIntent(MovieDetailIntent.LoadNextReviewsPage)
        }
    }

    val density = androidx.compose.ui.platform.LocalDensity.current
    val progressProvider = remember {
        {
            if (listState.firstVisibleItemIndex > 0) {
                1f
            } else {
                val scrollOffsetPx = listState.firstVisibleItemScrollOffset.toFloat()
                val maxScrollPx = with(density) { (280.dp - 56.dp).toPx() }
                if (maxScrollPx > 0f) {
                    (scrollOffsetPx / maxScrollPx).coerceIn(minimumValue = 0f, maximumValue = 1f)
                } else {
                    0f
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(tag = TestTags.MOVIE_DETAIL_CONTENT)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(bottom = Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
        ) {
            item {
                Spacer(modifier = Modifier.height(height = 280.dp))
            }

            item {
                MovieMetadataSection(
                    movie = state.movie,
                    showTitle = false
                )
            }

            item {
                MovieTrailerSection(
                    modifier = Modifier.padding(horizontal = Dimens.PaddingMedium),
                    state = state,
                )
            }

            state.movie?.overview?.let { overview ->
                if (overview.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.PaddingMedium)
                        ) {
                            Text(
                                text = overview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.PaddingMedium)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Dimens.PaddingNormal),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    )
                    Text(
                        text = stringResource(id = R.string.detail_reviews_header),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (state.reviews.isEmpty() && !state.isLoadingReviews && state.errorReviews == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = Dimens.PaddingMedium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.detail_no_reviews),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            items(
                items = state.reviews,
                key = { it.id }
            ) { review ->
                ReviewItem(
                    review = review,
                    modifier = Modifier.padding(horizontal = Dimens.PaddingMedium)
                )
            }

            if (state.isLoadingReviews) {
                item {
                    ReviewsShimmer(
                        modifier = Modifier
                            .padding(horizontal = Dimens.PaddingMedium)
                            .testTag(tag = TestTags.PAGINATION_LOADING_FOOTER)
                    )
                }
            }

            if (state.errorReviews != null) {
                item {
                    PaginationErrorRow(
                        message = state.errorReviews.asString(),
                        onRetry = { onIntent(MovieDetailIntent.RetryReviews) },
                        modifier = Modifier
                            .padding(horizontal = Dimens.PaddingMedium)
                            .testTag(tag = TestTags.PAGINATION_ERROR_FOOTER)
                    )
                }
            }
        }

        BackdropBanner(
            movie = state.movie,
            showActions = false,
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val progressVal = progressProvider()
                    val heightDp = 280.dp - (224.dp * progressVal)
                    val heightPx = heightDp.roundToPx()
                    val placeable = measurable.measure(
                        constraints.copy(
                            minHeight = heightPx,
                            maxHeight = heightPx
                        )
                    )
                    layout(width = placeable.width, height = placeable.height) {
                        placeable.placeRelative(x = 0, y = 0)
                    }
                }
                .align(alignment = Alignment.TopCenter)
        )

        StickyTopBar(
            title = state.movie?.title ?: "",
            isFavorite = state.isFavorite,
            onFavoriteToggle = { onIntent(MovieDetailIntent.ToggleFavorite) },
            onBackClick = onBackClick,
            progressProvider = progressProvider,
            modifier = Modifier.align(alignment = Alignment.TopCenter)
        )
    }
}

@Composable
fun ExpandedDetailLayout(
    modifier: Modifier = Modifier,
    state: MovieDetailState,
    onIntent: (MovieDetailIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(tag = TestTags.MOVIE_DETAIL_CONTENT)
    ) {
        BackdropBanner(
            movie = state.movie,
            isFavorite = state.isFavorite,
            onFavoriteToggle = { onIntent(MovieDetailIntent.ToggleFavorite) },
            onBackClick = onBackClick
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f),
            horizontalArrangement = Arrangement.spacedBy(space = Dimens.PaddingLarge)
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxHeight()
                    .verticalScroll(state = rememberScrollState())
                    .padding(all = Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal)
            ) {
                MovieMetadataSection(movie = state.movie)

                MovieTrailerSection(state = state)

                state.movie?.overview?.let { overview ->
                    if (overview.isNotEmpty()) {
                        Text(
                            text = overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            val listState = rememberLazyListState()
            val shouldLoadMore = remember {
                derivedStateOf {
                    val lastVisibleItemIndex =
                        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val totalItemsCount = listState.layoutInfo.totalItemsCount
                    totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 2
                }
            }

            LaunchedEffect(key1 = shouldLoadMore.value) {
                if (shouldLoadMore.value) {
                    onIntent(MovieDetailIntent.LoadNextReviewsPage)
                }
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(all = Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
                modifier = Modifier
                    .weight(weight = 1.2f)
                    .fillMaxHeight()
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.detail_reviews_header),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
                    )
                }

                if (state.reviews.isEmpty() && !state.isLoadingReviews && state.errorReviews == null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.PaddingLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.detail_no_reviews),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                items(
                    items = state.reviews,
                    key = { it.id }
                ) { review ->
                    ReviewItem(review = review)
                }

                if (state.isLoadingReviews) {
                    item {
                        ReviewsShimmer(
                            modifier = Modifier.testTag(tag = TestTags.PAGINATION_LOADING_FOOTER)
                        )
                    }
                }

                if (state.errorReviews != null) {
                    item {
                        PaginationErrorRow(
                            message = state.errorReviews.asString(),
                            onRetry = { onIntent(MovieDetailIntent.RetryReviews) },
                            modifier = Modifier.testTag(tag = TestTags.PAGINATION_ERROR_FOOTER)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackdropBanner(
    modifier: Modifier = Modifier,
    movie: Movie?,
    isFavorite: Boolean = false,
    onFavoriteToggle: () -> Unit = {},
    onBackClick: () -> Unit = {},
    showActions: Boolean = true,
) {
    val backdropUrl = movie?.backdropPath?.let { path -> "https://image.tmdb.org/t/p/w780$path" }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 280.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(data = backdropUrl)
                .crossfade(enable = true)
                .build(),
            contentDescription = movie?.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.5f)
                .align(alignment = Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        if (showActions) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingNormal)
                    .align(alignment = Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.desc_back),
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .testTag(tag = TestTags.FAVORITE_TOGGLE)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(id = R.string.desc_toggle_favorite),
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun StickyTopBar(
    modifier: Modifier = Modifier,
    title: String,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onBackClick: () -> Unit,
    progressProvider: () -> Float
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 56.dp)
            .drawBehind {
                drawRect(color = backgroundColor.copy(alpha = 0.85f * progressProvider()))
            }
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .padding(start = Dimens.PaddingSmall)
                .drawBehind {
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.5f * (1f - progressProvider())),
                        radius = size.minDimension / 2f
                    )
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.desc_back),
                tint = Color.White
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .graphicsLayer {
                    val progressVal = progressProvider()
                    val scaleVal = 1f - 0.25f * progressVal
                    this.scaleX = scaleVal
                    this.scaleY = scaleVal
                    this.translationX =
                        with(density) { (16.dp + (72.dp - 16.dp) * progressVal).toPx() }
                    this.translationY = with(density) { (268.dp * (1f - progressVal)).toPx() }
                    this.transformOrigin = androidx.compose.ui.graphics.TransformOrigin(
                        pivotFractionX = 0f,
                        pivotFractionY = 0.5f
                    )
                }
                .padding(end = 64.dp)
        )

        IconButton(
            onClick = onFavoriteToggle,
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .padding(end = Dimens.PaddingSmall)
                .testTag(tag = TestTags.FAVORITE_TOGGLE)
                .drawBehind {
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.5f * (1f - progressProvider())),
                        radius = size.minDimension / 2f
                    )
                }
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(id = R.string.desc_toggle_favorite),
                tint = if (isFavorite) Color.Red else Color.White
            )
        }
    }
}

@Composable
fun MovieMetadataSection(
    modifier: Modifier = Modifier,
    movie: Movie?,
    showTitle: Boolean = true,
) {
    if (movie == null) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingTiny)
    ) {
        if (showTitle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(weight = 1f)
                )
                Spacer(modifier = Modifier.width(width = Dimens.PaddingMedium))
                RatingBadge(rating = movie.voteAverage)
            }
        } else {
            Spacer(modifier = Modifier.height(height = 32.dp))
        }

        val runtimeText = movie.runtime?.let { mins ->
            stringResource(
                id = R.string.detail_runtime,
                formatArgs = arrayOf(mins)
            )
        }
        val releaseDateText = movie.releaseDate?.let { date ->
            stringResource(
                id = R.string.detail_release_date,
                formatArgs = arrayOf(date)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!showTitle) {
                RatingBadge(rating = movie.voteAverage)
            }
            if (releaseDateText != null) {
                Text(
                    text = releaseDateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            if (runtimeText != null) {
                Text(
                    text = runtimeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        val genresText = movie.genres?.joinToString(separator = ", ") { it.name }
        if (!genresText.isNullOrEmpty()) {
            Text(
                text = stringResource(
                    id = R.string.detail_genres,
                    formatArgs = arrayOf(genresText)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun MovieTrailerSection(
    modifier: Modifier = Modifier,
    state: MovieDetailState
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 16f / 9f)
            .clip(shape = RoundedCornerShape(size = Dimens.CornerMedium))
    ) {
        when {
            state.isLoadingTrailers -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect()
                )
            }

            state.errorTrailers != null -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.errorTrailers.asString(),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(all = Dimens.PaddingMedium)
                        )
                    }
                }
            }

            state.trailers.isEmpty() -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.4f
                        )
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.detail_no_trailer),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            else -> {
                val trailerKey = state.trailers.firstOrNull()?.key
                if (trailerKey != null) {
                    YouTubePlayer(
                        videoId = trailerKey,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun YouTubePlayer(
    modifier: Modifier = Modifier,
    videoId: String
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context = context).apply {
                enableAutomaticInitialization = false
                lifecycleOwner.lifecycle.addObserver(observer = this)
                val options = IFramePlayerOptions.Builder(context = context)
                    .origin(origin = "https://${context.packageName}")
                    .build()
                initialize(
                    youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.cueVideo(videoId = videoId, startSeconds = 0f)
                        }
                    },
                    playerOptions = options
                )
            }
        },
        onRelease = { view ->
            lifecycleOwner.lifecycle.removeObserver(observer = view)
            view.release()
        },
        modifier = modifier
    )
}

@Composable
fun ReviewItem(
    modifier: Modifier = Modifier,
    review: Review
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(size = Dimens.CornerMedium),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                shape = RoundedCornerShape(size = Dimens.CornerMedium)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = Dimens.PaddingNormal)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                review.authorDetails?.rating?.let { rating ->
                    RatingBadge(rating = rating)
                }
            }
            Spacer(modifier = Modifier.height(height = Dimens.PaddingSmall))
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis
            )
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
        shape = RoundedCornerShape(size = Dimens.CornerMedium),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                shape = RoundedCornerShape(size = Dimens.CornerMedium)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = Dimens.PaddingNormal),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(weight = 1f)
            )
            Spacer(modifier = Modifier.width(width = Dimens.PaddingMedium))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(size = Dimens.CornerNormal)
            ) {
                Text(
                    text = stringResource(id = R.string.btn_retry),
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun DetailShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingMedium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 280.dp)
                .shimmerEffect()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.6f)
                    .height(height = 24.dp)
                    .clip(shape = RoundedCornerShape(size = Dimens.CornerSmall))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.4f)
                    .height(height = 16.dp)
                    .clip(shape = RoundedCornerShape(size = Dimens.CornerSmall))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 150.dp)
                    .clip(shape = RoundedCornerShape(size = Dimens.CornerMedium))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 100.dp)
                    .clip(shape = RoundedCornerShape(size = Dimens.CornerMedium))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun ReviewsShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.PaddingSmall),
        verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingNormal)
    ) {
        repeat(times = 2) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(size = Dimens.CornerMedium),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = Dimens.BorderThin,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(size = Dimens.CornerMedium)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = Dimens.PaddingNormal),
                    verticalArrangement = Arrangement.spacedBy(space = Dimens.PaddingSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .width(width = 100.dp)
                            .height(height = 16.dp)
                            .clip(shape = RoundedCornerShape(size = Dimens.CornerSmall))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = 40.dp)
                            .clip(shape = RoundedCornerShape(size = Dimens.CornerSmall))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}
