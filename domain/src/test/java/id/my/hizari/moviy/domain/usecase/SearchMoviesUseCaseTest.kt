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

class SearchMoviesUseCaseTest {

    @Test
    fun invoke_returnsMoviesFromRepository() {
        runBlocking {
            val expectedMovies = listOf(
                Movie(
                    id = 1,
                    title = "Searched Movie",
                    overview = "Overview",
                    posterPath = null,
                    backdropPath = null,
                    releaseDate = null,
                    voteAverage = 7.5
                )
            )
            val repository = mockk<MovieRepository>()
            coEvery { repository.searchMovies(query = "action", page = 1) } returns expectedMovies

            val useCase = SearchMoviesUseCase(repository)

            val actualMovies = useCase(query = "action", page = 1)

            assertEquals(expectedMovies, actualMovies)
        }
    }
}
