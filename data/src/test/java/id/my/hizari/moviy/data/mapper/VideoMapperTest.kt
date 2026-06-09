/**
 * id.my.hizari.moviy.data.mapper
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.mapper

import id.my.hizari.moviy.data.model.VideoDto
import org.junit.Assert.assertEquals
import org.junit.Test

class VideoMapperTest {

    @Test
    fun toDomain_mapsCorrectly() {
        val dto = VideoDto(
            id = "v123",
            key = "dQw4w9WgXcQ",
            name = "Official Trailer",
            site = "YouTube",
            type = "Trailer"
        )
        val domain = dto.toDomain()

        assertEquals("v123", domain.id)
        assertEquals("dQw4w9WgXcQ", domain.key)
        assertEquals("Official Trailer", domain.name)
        assertEquals("YouTube", domain.site)
        assertEquals("Trailer", domain.type)
    }
}
