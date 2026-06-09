/**
 * id.my.hizari.moviy.ui.discover
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.discover

import androidx.lifecycle.SavedStateHandle
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.usecase.GetDiscoverMoviesUseCase
import id.my.hizari.moviy.navigation.NavigationArgs
import id.my.hizari.moviy.ui.components.UiText
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: GetDiscoverMoviesUseCase = mockk()
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(
            mapOf(
                NavigationArgs.GENRE_ID to "28",
                NavigationArgs.GENRE_NAME to "Action"
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsFirstPageSuccessfully() = runTest {
        val expectedMovies = listOf(
            Movie(
                id = 1,
                title = "Action Movie 1",
                overview = "Action Overview 1",
                posterPath = "/post1.jpg",
                backdropPath = "/back1.jpg",
                releaseDate = "2026-06-09",
                voteAverage = 8.2
            )
        )
        coEvery { useCase("28", 1) } returns expectedMovies

        val viewModel = DiscoverViewModel(useCase, savedStateHandle)

        assertTrue(viewModel.state.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(expectedMovies, state.movies)
        assertEquals(1, state.currentPage)
        assertNull(state.error)
        assertFalse(state.isLastPage)
    }

    @Test
    fun init_loadsFirstPageError() = runTest {
        coEvery { useCase("28", 1) } throws RuntimeException("Network Error")

        val viewModel = DiscoverViewModel(useCase, savedStateHandle)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.movies.isEmpty())
        assertEquals(UiText.DynamicString("Network Error"), state.error)
    }

    @Test
    fun loadNextPage_success() = runTest {
        val page1Movies = listOf(
            Movie(1, "Movie 1", "Overview 1", null, null, null, 7.0)
        )
        val page2Movies = listOf(
            Movie(2, "Movie 2", "Overview 2", null, null, null, 8.0)
        )
        coEvery { useCase("28", 1) } returns page1Movies
        coEvery { useCase("28", 2) } returns page2Movies

        val viewModel = DiscoverViewModel(useCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(DiscoverIntent.LoadNextPage)
        assertTrue(viewModel.state.value.isLoadingNextPage)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoadingNextPage)
        assertEquals(2, state.currentPage)
        assertEquals(page1Movies + page2Movies, state.movies)
        assertNull(state.paginationError)
    }

    @Test
    fun loadNextPage_error() = runTest {
        val page1Movies = listOf(
            Movie(1, "Movie 1", "Overview 1", null, null, null, 7.0)
        )
        coEvery { useCase("28", 1) } returns page1Movies
        coEvery { useCase("28", 2) } throws RuntimeException("Pagination Failed")

        val viewModel = DiscoverViewModel(useCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(DiscoverIntent.LoadNextPage)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoadingNextPage)
        assertEquals(1, state.currentPage)
        assertEquals(page1Movies, state.movies)
        assertEquals(UiText.DynamicString("Pagination Failed"), state.paginationError)
    }

    @Test
    fun retry_firstPageRetry_success() = runTest {
        val expectedMovies = listOf(
            Movie(1, "Movie 1", "Overview 1", null, null, null, 7.0)
        )
        var firstCall = true
        coEvery { useCase("28", 1) } coAnswers {
            if (firstCall) {
                firstCall = false
                throw RuntimeException("Initial failure")
            } else {
                expectedMovies
            }
        }

        val viewModel = DiscoverViewModel(useCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.error != null)

        viewModel.handleIntent(DiscoverIntent.Retry)
        assertTrue(viewModel.state.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(expectedMovies, state.movies)
    }

    @Test
    fun retry_paginationRetry_success() = runTest {
        val page1Movies = listOf(
            Movie(1, "Movie 1", "Overview 1", null, null, null, 7.0)
        )
        val page2Movies = listOf(
            Movie(2, "Movie 2", "Overview 2", null, null, null, 8.0)
        )
        coEvery { useCase("28", 1) } returns page1Movies
        var firstCallPage2 = true
        coEvery { useCase("28", 2) } coAnswers {
            if (firstCallPage2) {
                firstCallPage2 = false
                throw RuntimeException("Next failure")
            } else {
                page2Movies
            }
        }

        val viewModel = DiscoverViewModel(useCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(DiscoverIntent.LoadNextPage)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.paginationError != null)

        viewModel.handleIntent(DiscoverIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.paginationError)
        assertEquals(page1Movies + page2Movies, state.movies)
        assertEquals(2, state.currentPage)
    }
}
