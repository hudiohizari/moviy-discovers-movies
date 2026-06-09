/**
 * id.my.hizari.moviy.ui.genres
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.genres

import id.my.hizari.moviy.domain.model.Genre
import id.my.hizari.moviy.domain.usecase.GetGenresUseCase
import id.my.hizari.moviy.ui.components.UiText
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GenreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsGenresSuccessfully() = runTest {
        val expectedGenres = listOf(Genre(1, "Action"), Genre(2, "Comedy"))
        val useCase = mockk<GetGenresUseCase>()
        coEvery { useCase() } returns expectedGenres

        val viewModel = GenreViewModel(useCase)

        assertTrue(viewModel.state.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(expectedGenres, state.genres)
        assertEquals(null, state.error)
    }

    @Test
    fun init_loadsGenresError() = runTest {
        val useCase = mockk<GetGenresUseCase>()
        coEvery { useCase() } throws RuntimeException("Network Error")

        val viewModel = GenreViewModel(useCase)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.genres.isEmpty())
        assertEquals(UiText.DynamicString("Network Error"), state.error)
    }
}
