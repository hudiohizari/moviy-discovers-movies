/**
 * id.my.hizari.moviy.ui.search
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.search

import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.usecase.SearchMoviesUseCase
import id.my.hizari.moviy.ui.components.UiText
import io.mockk.coEvery
import io.mockk.coVerify
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: SearchMoviesUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_stateIsEmpty() = runTest(testDispatcher) {
        val viewModel = SearchViewModel(useCase)
        testDispatcher.scheduler.runCurrent()
        val state = viewModel.state.value

        assertEquals("", state.query)
        assertFalse(state.isLoading)
        assertTrue(state.movies.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun queryChanged_debouncesAndSearches() = runTest(testDispatcher) {
        val expectedMovies = listOf(
            Movie(1, "Search 1", "Overview 1", null, null, null, 7.0)
        )
        coEvery { useCase("Mandalorian", 1) } coAnswers {
            kotlinx.coroutines.delay(100)
            expectedMovies
        }

        val viewModel = SearchViewModel(useCase)
        testDispatcher.scheduler.runCurrent()

        // Type query
        viewModel.handleIntent(SearchIntent.QueryChanged("Mandalorian"))
        assertEquals("Mandalorian", viewModel.state.value.query)
        assertFalse(viewModel.state.value.isLoading)

        // Advance time by 400ms (debounce is 500ms)
        testDispatcher.scheduler.advanceTimeBy(400)
        assertFalse(viewModel.state.value.isLoading)

        // Advance time past 500ms threshold
        testDispatcher.scheduler.advanceTimeBy(100)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.isLoading)

        // Complete job execution
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(expectedMovies, state.movies)
        assertNull(state.error)
    }

    @Test
    fun queryChanged_multipleKeystrokesDebouncedToOneRequest() = runTest(testDispatcher) {
        val expectedMovies = listOf(
            Movie(1, "Search 1", "Overview 1", null, null, null, 7.0)
        )
        coEvery { useCase("Mandalorian", 1) } returns expectedMovies

        val viewModel = SearchViewModel(useCase)
        testDispatcher.scheduler.runCurrent()

        // Simulate fast typing
        viewModel.handleIntent(SearchIntent.QueryChanged("M"))
        testDispatcher.scheduler.advanceTimeBy(200)
        viewModel.handleIntent(SearchIntent.QueryChanged("Ma"))
        testDispatcher.scheduler.advanceTimeBy(200)
        viewModel.handleIntent(SearchIntent.QueryChanged("Mandalorian"))

        // Wait past 500ms debounce since last keystroke
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { useCase("Mandalorian", 1) }
        coVerify(exactly = 0) { useCase("M", any()) }
        coVerify(exactly = 0) { useCase("Ma", any()) }
    }

    @Test
    fun loadNextPage_success() = runTest(testDispatcher) {
        val page1 = listOf(Movie(1, "Movie 1", "Overview 1", null, null, null, 7.0))
        val page2 = listOf(Movie(2, "Movie 2", "Overview 2", null, null, null, 8.0))

        coEvery { useCase("Mandalorian", 1) } returns page1
        coEvery { useCase("Mandalorian", 2) } returns page2

        val viewModel = SearchViewModel(useCase)
        testDispatcher.scheduler.runCurrent()
        viewModel.handleIntent(SearchIntent.QueryChanged("Mandalorian"))
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(SearchIntent.LoadNextPage)
        assertTrue(viewModel.state.value.isLoadingNextPage)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoadingNextPage)
        assertEquals(2, state.currentPage)
        assertEquals(page1 + page2, state.movies)
    }

    @Test
    fun retry_firstPageError() = runTest(testDispatcher) {
        val expected = listOf(Movie(1, "Movie 1", "Overview 1", null, null, null, 7.0))
        var firstCall = true
        coEvery { useCase("Mandalorian", 1) } coAnswers {
            if (firstCall) {
                firstCall = false
                throw RuntimeException("Network Error")
            } else {
                expected
            }
        }

        val viewModel = SearchViewModel(useCase)
        testDispatcher.scheduler.runCurrent()
        viewModel.handleIntent(SearchIntent.QueryChanged("Mandalorian"))
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.error != null)

        viewModel.handleIntent(SearchIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.error)
        assertEquals(expected, state.movies)
    }
}
