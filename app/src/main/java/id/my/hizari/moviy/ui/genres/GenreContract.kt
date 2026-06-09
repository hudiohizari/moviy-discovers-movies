/**
 * id.my.hizari.moviy.ui.genres
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.genres

import id.my.hizari.moviy.domain.model.Genre
import id.my.hizari.moviy.ui.components.UiText

sealed interface GenreIntent {
    data object LoadGenres : GenreIntent
    data object Retry : GenreIntent
}

data class GenreState(
    val isLoading: Boolean = false,
    val genres: List<Genre> = emptyList(),
    val error: UiText? = null
)
