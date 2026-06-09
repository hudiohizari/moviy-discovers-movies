/**
 * id.my.hizari.moviy.data.mapper
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.mapper

import id.my.hizari.moviy.data.model.GenreDto
import id.my.hizari.moviy.data.model.MovieDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MovieMapperTest {

    @Test
    fun toDomain_mapsCorrectly() {
        val genres = listOf(GenreDto(id = 28, name = "Action"))
        val dto = MovieDto(
            id = 550,
            title = "Fight Club",
            overview = "An insomniac office worker...",
            posterPath = "/path/to/poster.jpg",
            backdropPath = "/path/to/backdrop.jpg",
            releaseDate = "1999-10-15",
            voteAverage = 8.4,
            runtime = 139,
            genres = genres
        )
        val domain = dto.toDomain()

        assertEquals(550, domain.id)
        assertEquals("Fight Club", domain.title)
        assertEquals("An insomniac office worker...", domain.overview)
        assertEquals("/path/to/poster.jpg", domain.posterPath)
        assertEquals("/path/to/backdrop.jpg", domain.backdropPath)
        assertEquals("1999-10-15", domain.releaseDate)
        assertEquals(8.4, domain.voteAverage, 0.01)
        assertEquals(139, domain.runtime)
        assertNotNull(domain.genres)
        assertEquals(1, domain.genres?.size)
        assertEquals(28, domain.genres?.first()?.id)
        assertEquals("Action", domain.genres?.first()?.name)
    }
}
