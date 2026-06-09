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
import id.my.hizari.moviy.domain.model.AuthorDetails
import id.my.hizari.moviy.domain.model.Review

fun ReviewDto.toDomain(): Review {
    return Review(
        id = id,
        author = author,
        authorDetails = authorDetails?.toDomain(),
        content = content,
        createdAt = createdAt
    )
}

fun AuthorDetailsDto.toDomain(): AuthorDetails {
    return AuthorDetails(
        name = name,
        username = username,
        avatarPath = avatarPath,
        rating = rating
    )
}
