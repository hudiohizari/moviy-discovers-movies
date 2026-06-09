/**
 * id.my.hizari.moviy.ui.search
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.search

import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.ui.components.UiText

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object LoadNextPage : SearchIntent
    data object Retry : SearchIntent
}

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 1,
    val isLastPage: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val error: UiText? = null,
    val paginationError: UiText? = null
)
