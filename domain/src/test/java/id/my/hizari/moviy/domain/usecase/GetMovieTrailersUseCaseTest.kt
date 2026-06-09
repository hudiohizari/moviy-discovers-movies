/**
 * id.my.hizari.moviy.domain.usecase
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.usecase

import id.my.hizari.moviy.domain.model.Video
import id.my.hizari.moviy.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMovieTrailersUseCaseTest {

    @Test
    fun invoke_filtersAndReturnsOnlyYouTubeTrailers() = runBlocking {
        val allVideos = listOf(
            Video(
                id = "vid1",
                key = "key1",
                name = "Official Trailer",
                site = "YouTube",
                type = "Trailer"
            ),
            Video(
                id = "vid2",
                key = "key2",
                name = "Teaser Video",
                site = "YouTube",
                type = "Teaser"
            ),
            Video(
                id = "vid3",
                key = "key3",
                name = "Vimeo Trailer",
                site = "Vimeo",
                type = "Trailer"
            )
        )
        val expectedTrailers = listOf(
            Video(
                id = "vid1",
                key = "key1",
                name = "Official Trailer",
                site = "YouTube",
                type = "Trailer"
            )
        )
        val repository = mockk<MovieRepository>()
        coEvery { repository.getMovieTrailers(movieId = 123) } returns allVideos

        val useCase = GetMovieTrailersUseCase(repository = repository)

        val actualTrailers = useCase(movieId = 123)

        assertEquals(expectedTrailers, actualTrailers)
    }
}
