/**
 * id.my.hizari.moviy.domain.usecase
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.usecase

import id.my.hizari.moviy.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class IsFavoriteUseCaseTest {

    @Test
    fun invoke_returnsIsFavoriteFromRepository() = runBlocking {
        val movieId = 123
        val repository = mockk<MovieRepository>()
        coEvery { repository.isFavorite(movieId = movieId) } returns flowOf(value = true)

        val useCase = IsFavoriteUseCase(repository = repository)

        val isFavorite = useCase(movieId = movieId).first()

        assertTrue(isFavorite)
    }
}
