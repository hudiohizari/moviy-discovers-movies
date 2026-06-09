/**
 * id.my.hizari.moviy.data.mapper
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.mapper

import id.my.hizari.moviy.data.model.AuthorDetailsDto
import id.my.hizari.moviy.data.model.ReviewDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ReviewMapperTest {

    @Test
    fun toDomain_mapsCorrectly() {
        val authorDetailsDto = AuthorDetailsDto(
            name = "John Doe",
            username = "johndoe",
            avatarPath = "/path/to/avatar.jpg",
            rating = 9.0
        )
        val dto = ReviewDto(
            id = "r123",
            author = "John Doe",
            authorDetails = authorDetailsDto,
            content = "This was a masterpiece!",
            createdAt = "2026-06-09T00:00:00.000Z"
        )
        val domain = dto.toDomain()

        assertEquals("r123", domain.id)
        assertEquals("John Doe", domain.author)
        assertEquals("This was a masterpiece!", domain.content)
        assertEquals("2026-06-09T00:00:00.000Z", domain.createdAt)

        val authorDetails = domain.authorDetails
        assertNotNull(authorDetails)
        assertEquals("John Doe", authorDetails?.name)
        assertEquals("johndoe", authorDetails?.username)
        assertEquals("/path/to/avatar.jpg", authorDetails?.avatarPath)
        assertEquals(9.0, authorDetails?.rating!!, 0.01)
    }
}
