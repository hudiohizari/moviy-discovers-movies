/**
 * id.my.hizari.moviy.ui.detail
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.detail

import androidx.lifecycle.SavedStateHandle
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.model.Review
import id.my.hizari.moviy.domain.model.Video
import id.my.hizari.moviy.domain.usecase.GetMovieDetailsUseCase
import id.my.hizari.moviy.domain.usecase.GetMovieReviewsUseCase
import id.my.hizari.moviy.domain.usecase.GetMovieTrailersUseCase
import id.my.hizari.moviy.domain.usecase.IsFavoriteUseCase
import id.my.hizari.moviy.domain.usecase.ToggleFavoriteUseCase
import id.my.hizari.moviy.navigation.NavigationArgs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getMovieDetailsUseCase: GetMovieDetailsUseCase = mockk()
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase = mockk()
    private val getMovieTrailersUseCase: GetMovieTrailersUseCase = mockk()
    private val isFavoriteUseCase: IsFavoriteUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
        savedStateHandle = SavedStateHandle(
            initialState = mapOf(
                NavigationArgs.MOVIE_ID to 123
            )
        )
        coEvery { isFavoriteUseCase(movieId = 123) } returns flowOf(value = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsDetailsAndTrailersAndReviews() = runTest(context = testDispatcher) {
        val movie = Movie(
            id = 123,
            title = "Movie Detail",
            overview = "Overview Detail",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2026-06-09",
            voteAverage = 8.5
        )
        val trailers = listOf(
            Video(
                id = "vid1",
                key = "key1",
                name = "Trailer 1",
                site = "YouTube",
                type = "Trailer"
            )
        )
        val reviews = listOf(
            Review(
                id = "rev1",
                author = "Author 1",
                authorDetails = null,
                content = "Content 1",
                createdAt = "2026-06-09"
            )
        )

        coEvery { getMovieDetailsUseCase(movieId = 123) } returns movie
        coEvery { getMovieTrailersUseCase(movieId = 123) } returns trailers
        coEvery { getMovieReviewsUseCase(movieId = 123, page = 1) } returns reviews

        val viewModel = MovieDetailViewModel(
            getMovieDetailsUseCase = getMovieDetailsUseCase,
            getMovieReviewsUseCase = getMovieReviewsUseCase,
            getMovieTrailersUseCase = getMovieTrailersUseCase,
            isFavoriteUseCase = isFavoriteUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            savedStateHandle = savedStateHandle
        )

        // Assert initial loading state
        assertTrue(viewModel.state.value.isLoadingDetails)
        assertTrue(viewModel.state.value.isLoadingReviews)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoadingDetails)
        assertFalse(state.isLoadingReviews)
        assertEquals(movie, state.movie)
        assertEquals(trailers, state.trailers)
        assertEquals(reviews, state.reviews)
        assertTrue(state.isFavorite)
        assertNull(state.errorDetails)
        assertNull(state.errorReviews)
    }

    @Test
    fun loadNextReviewsPage_success() = runTest(context = testDispatcher) {
        val movie = Movie(
            id = 123,
            title = "Movie",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            voteAverage = 7.0
        )
        val reviews1 = listOf(
            Review(
                id = "rev1",
                author = "Author 1",
                authorDetails = null,
                content = "Content 1",
                createdAt = null
            )
        )
        val reviews2 = listOf(
            Review(
                id = "rev2",
                author = "Author 2",
                authorDetails = null,
                content = "Content 2",
                createdAt = null
            )
        )

        coEvery { getMovieDetailsUseCase(movieId = 123) } returns movie
        coEvery { getMovieTrailersUseCase(movieId = 123) } returns emptyList()
        coEvery { getMovieReviewsUseCase(movieId = 123, page = 1) } returns reviews1
        coEvery { getMovieReviewsUseCase(movieId = 123, page = 2) } returns reviews2

        val viewModel = MovieDetailViewModel(
            getMovieDetailsUseCase = getMovieDetailsUseCase,
            getMovieReviewsUseCase = getMovieReviewsUseCase,
            getMovieTrailersUseCase = getMovieTrailersUseCase,
            isFavoriteUseCase = isFavoriteUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            savedStateHandle = savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(intent = MovieDetailIntent.LoadNextReviewsPage)
        assertTrue(viewModel.state.value.isLoadingReviews)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoadingReviews)
        assertEquals(2, state.reviewsPage)
        assertEquals(reviews1 + reviews2, state.reviews)
    }

    @Test
    fun toggleFavorite_callsRepository() = runTest(context = testDispatcher) {
        val movie = Movie(
            id = 123,
            title = "Movie",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            voteAverage = 7.0
        )
        coEvery { getMovieDetailsUseCase(movieId = 123) } returns movie
        coEvery { getMovieTrailersUseCase(movieId = 123) } returns emptyList()
        coEvery { getMovieReviewsUseCase(movieId = 123, page = 1) } returns emptyList()
        coEvery { toggleFavoriteUseCase(movie = movie) } returns true

        val viewModel = MovieDetailViewModel(
            getMovieDetailsUseCase = getMovieDetailsUseCase,
            getMovieReviewsUseCase = getMovieReviewsUseCase,
            getMovieTrailersUseCase = getMovieTrailersUseCase,
            isFavoriteUseCase = isFavoriteUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            savedStateHandle = savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(intent = MovieDetailIntent.ToggleFavorite)
        testDispatcher.scheduler.runCurrent()

        coVerify(exactly = 1) { toggleFavoriteUseCase(movie = movie) }
    }
}
