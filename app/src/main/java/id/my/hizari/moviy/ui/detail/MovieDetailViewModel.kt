/**
 * id.my.hizari.moviy.ui.detail
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.moviy.domain.usecase.GetMovieDetailsUseCase
import id.my.hizari.moviy.domain.usecase.GetMovieReviewsUseCase
import id.my.hizari.moviy.domain.usecase.GetMovieTrailersUseCase
import id.my.hizari.moviy.domain.usecase.IsFavoriteUseCase
import id.my.hizari.moviy.domain.usecase.ToggleFavoriteUseCase
import id.my.hizari.moviy.navigation.NavigationArgs
import id.my.hizari.moviy.ui.components.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    private val getMovieTrailersUseCase: GetMovieTrailersUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(value = MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    private var movieId: Int = 0

    init {
        val id = savedStateHandle.get<Int>(NavigationArgs.MOVIE_ID)
        if (id != null) {
            movieId = id
            viewModelScope.launch {
                isFavoriteUseCase(movieId = movieId).collect { isFav ->
                    _state.update { it.copy(isFavorite = isFav) }
                }
            }
            handleIntent(intent = MovieDetailIntent.LoadDetails)
        }
    }

    fun handleIntent(intent: MovieDetailIntent) {
        when (intent) {
            MovieDetailIntent.LoadDetails -> loadDetails()
            MovieDetailIntent.ToggleFavorite -> toggleFavorite()
            MovieDetailIntent.LoadNextReviewsPage -> loadReviewsPage(page = _state.value.reviewsPage + 1)
            MovieDetailIntent.RetryDetails -> loadDetails()
            MovieDetailIntent.RetryReviews -> loadReviewsPage(page = _state.value.reviewsPage)
        }
    }

    private fun loadDetails() {
        _state.update {
            it.copy(
                isLoadingDetails = true,
                errorDetails = null,
                trailers = emptyList(),
                isLoadingTrailers = true,
                errorTrailers = null,
                reviews = emptyList(),
                reviewsPage = 1,
                isReviewsLastPage = false,
                isLoadingReviews = false,
                errorReviews = null
            )
        }

        viewModelScope.launch {
            try {
                val movie = getMovieDetailsUseCase(movieId = movieId)
                _state.update { it.copy(movie = movie, isLoadingDetails = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingDetails = false,
                        errorDetails = e.toUiText()
                    )
                }
            }
        }

        viewModelScope.launch {
            try {
                val trailers = getMovieTrailersUseCase(movieId = movieId)
                _state.update { it.copy(trailers = trailers, isLoadingTrailers = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingTrailers = false,
                        errorTrailers = e.toUiText()
                    )
                }
            }
        }

        loadReviewsPage(page = 1)
    }

    private fun loadReviewsPage(page: Int) {
        if (page > 1 && (_state.value.isLoadingReviews || _state.value.isReviewsLastPage)) return

        _state.update {
            it.copy(
                isLoadingReviews = true,
                errorReviews = null
            )
        }

        viewModelScope.launch {
            try {
                val newReviews = getMovieReviewsUseCase(movieId = movieId, page = page)
                _state.update {
                    it.copy(
                        isLoadingReviews = false,
                        reviews = if (page == 1) newReviews else it.reviews + newReviews,
                        reviewsPage = page,
                        isReviewsLastPage = newReviews.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingReviews = false,
                        errorReviews = e.toUiText()
                    )
                }
            }
        }
    }

    private fun toggleFavorite() {
        val movie = _state.value.movie ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(movie = movie)
        }
    }
}
