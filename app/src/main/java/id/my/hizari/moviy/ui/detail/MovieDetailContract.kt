/**
 * id.my.hizari.moviy.ui.detail
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.detail

import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.model.Review
import id.my.hizari.moviy.domain.model.Video
import id.my.hizari.moviy.ui.components.UiText

data class MovieDetailState(
    val movie: Movie? = null,
    val isLoadingDetails: Boolean = false,
    val errorDetails: UiText? = null,
    val trailers: List<Video> = emptyList(),
    val isLoadingTrailers: Boolean = false,
    val errorTrailers: UiText? = null,
    val isFavorite: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val reviewsPage: Int = 1,
    val isReviewsLastPage: Boolean = false,
    val isLoadingReviews: Boolean = false,
    val errorReviews: UiText? = null
)

sealed class MovieDetailIntent {
    object LoadDetails : MovieDetailIntent()
    object ToggleFavorite : MovieDetailIntent()
    object LoadNextReviewsPage : MovieDetailIntent()
    object RetryDetails : MovieDetailIntent()
    object RetryReviews : MovieDetailIntent()
}
