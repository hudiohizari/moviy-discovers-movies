/**
 * id.my.hizari.moviy.domain.usecase
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.usecase

import id.my.hizari.moviy.domain.model.Genre
import id.my.hizari.moviy.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetGenresUseCaseTest {

    @Test
    fun invoke_returnsGenresFromRepository() = runBlocking {
        val expectedGenres = listOf(
            Genre(1, "Action"),
            Genre(2, "Comedy")
        )
        val repository = mockk<MovieRepository>()
        coEvery { repository.getGenres() } returns expectedGenres

        val useCase = GetGenresUseCase(repository)

        val actualGenres = useCase()

        assertEquals(expectedGenres, actualGenres)
    }
}
