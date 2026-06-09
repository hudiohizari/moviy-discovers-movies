/**
 * id.my.hizari.moviy.data.mapper
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.mapper

import id.my.hizari.moviy.data.model.VideoDto
import id.my.hizari.moviy.domain.model.Video

fun VideoDto.toDomain(): Video {
    return Video(
        id = id,
        key = key,
        name = name,
        site = site,
        type = type
    )
}
