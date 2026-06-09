/**
 * id.my.hizari.moviy.ui.favorites
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.favorites

import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.usecase.GetFavoriteMoviesUseCase
import id.my.hizari.moviy.ui.components.UiText
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsFavoritesSuccessfully() = runTest(context = testDispatcher) {
        val expectedMovies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                posterPath = null,
                backdropPath = null,
                releaseDate = null,
                voteAverage = 7.5
            )
        )
        coEvery { getFavoriteMoviesUseCase() } returns flowOf(value = expectedMovies)

        val viewModel = FavoritesViewModel(getFavoriteMoviesUseCase = getFavoriteMoviesUseCase)

        assertTrue(viewModel.state.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(expectedMovies, state.movies)
        assertEquals(null, state.error)
    }

    @Test
    fun init_loadsFavoritesError() = runTest(context = testDispatcher) {
        coEvery { getFavoriteMoviesUseCase() } returns flow {
            throw RuntimeException("Database Error")
        }

        val viewModel = FavoritesViewModel(getFavoriteMoviesUseCase = getFavoriteMoviesUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.movies.isEmpty())
        assertEquals(UiText.DynamicString(value = "Database Error"), state.error)
    }
}
