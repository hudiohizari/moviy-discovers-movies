/**
 * id.my.hizari.moviy.domain.usecase
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.usecase

import id.my.hizari.moviy.domain.model.AuthorDetails
import id.my.hizari.moviy.domain.model.Review
import id.my.hizari.moviy.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMovieReviewsUseCaseTest {

    @Test
    fun invoke_returnsReviewsFromRepository() = runBlocking {
        val expectedReviews = listOf(
            Review(
                id = "rev123",
                author = "John Doe",
                authorDetails = AuthorDetails(
                    name = "John",
                    username = "johndoe",
                    avatarPath = "/path.jpg",
                    rating = 9.0
                ),
                content = "Great movie!",
                createdAt = "2026-06-09"
            )
        )
        val repository = mockk<MovieRepository>()
        coEvery { repository.getMovieReviews(movieId = 123, page = 1) } returns expectedReviews

        val useCase = GetMovieReviewsUseCase(repository = repository)

        val actualReviews = useCase(movieId = 123, page = 1)

        assertEquals(expectedReviews, actualReviews)
    }
}
