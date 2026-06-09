/**
 * id.my.hizari.moviy.domain.model
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.model

data class Review(
    val id: String,
    val author: String,
    val authorDetails: AuthorDetails?,
    val content: String,
    val createdAt: String?
)

data class AuthorDetails(
    val name: String?,
    val username: String,
    val avatarPath: String?,
    val rating: Double?
)
