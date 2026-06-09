/**
 * id.my.hizari.moviy.ui.discover
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.discover

import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.ui.components.UiText

sealed interface DiscoverIntent {
    data class LoadMovies(val genreId: String, val genreName: String) : DiscoverIntent
    data object LoadNextPage : DiscoverIntent
    data object Retry : DiscoverIntent
}

data class DiscoverState(
    val genreId: String? = null,
    val genreName: String = "",
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 1,
    val isLastPage: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val error: UiText? = null,
    val paginationError: UiText? = null
)
