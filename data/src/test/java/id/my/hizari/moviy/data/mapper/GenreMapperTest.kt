/**
 * id.my.hizari.moviy.data.mapper
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.mapper

import id.my.hizari.moviy.data.model.GenreDto
import org.junit.Assert.assertEquals
import org.junit.Test

class GenreMapperTest {

    @Test
    fun toDomain_mapsCorrectly() {
        val dto = GenreDto(id = 28, name = "Action")
        val domain = dto.toDomain()

        assertEquals(28, domain.id)
        assertEquals("Action", domain.name)
    }
}
