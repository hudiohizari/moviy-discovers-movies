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

class GetMovieDetailsUseCaseTest {

    @Test
    fun invoke_returnsMovieDetailsFromRepository() = runBlocking {
        val expectedMovie = Movie(
            id = 123,
            title = "Detail Movie",
            overview = "Detail Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2026-06-09",
            voteAverage = 8.8
        )
        val repository = mockk<MovieRepository>()
        coEvery { repository.getMovieDetails(movieId = 123) } returns expectedMovie

        val useCase = GetMovieDetailsUseCase(repository = repository)

        val actualMovie = useCase(movieId = 123)

        assertEquals(expectedMovie, actualMovie)
    }
}
