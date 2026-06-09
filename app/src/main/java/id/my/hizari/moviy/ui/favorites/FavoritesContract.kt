/**
 * id.my.hizari.moviy.ui.favorites
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.favorites

import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.ui.components.UiText

sealed interface FavoritesIntent {
    data object LoadFavorites : FavoritesIntent
}

data class FavoritesState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: UiText? = null
)
