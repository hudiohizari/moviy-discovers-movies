/**
 * id.my.hizari.moviy.data.mapper
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.mapper

import id.my.hizari.moviy.data.model.GenreDto
import id.my.hizari.moviy.domain.model.Genre

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}
