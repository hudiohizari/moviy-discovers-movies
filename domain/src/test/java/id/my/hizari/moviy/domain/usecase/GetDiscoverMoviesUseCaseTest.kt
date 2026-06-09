/**
 * id.my.hizari.moviy.domain.usecase
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.usecase

import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDiscoverMoviesUseCaseTest {

    @Test
    fun invoke_returnsMoviesFromRepository() = runBlocking {
        val expectedMovies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                posterPath = "/path1.jpg",
                backdropPath = "/backdrop1.jpg",
                releaseDate = "2026-06-09",
                voteAverage = 8.5
            )
        )
        val repository = mockk<MovieRepository>()
        coEvery { repository.discoverMovies("28", 1) } returns expectedMovies

        val useCase = GetDiscoverMoviesUseCase(repository)

        val actualMovies = useCase("28", 1)

        assertEquals(expectedMovies, actualMovies)
    }
}
