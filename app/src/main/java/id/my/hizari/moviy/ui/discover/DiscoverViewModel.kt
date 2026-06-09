/**
 * id.my.hizari.moviy.ui.discover
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.discover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.moviy.domain.usecase.GetDiscoverMoviesUseCase
import id.my.hizari.moviy.navigation.NavigationArgs
import id.my.hizari.moviy.ui.components.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

    init {
        val genreId = savedStateHandle.get<String>(NavigationArgs.GENRE_ID)
        val genreName = savedStateHandle.get<String>(NavigationArgs.GENRE_NAME)
        if (genreId != null && genreName != null) {
            handleIntent(DiscoverIntent.LoadMovies(genreId, genreName))
        }
    }

    fun handleIntent(intent: DiscoverIntent) {
        when (intent) {
            is DiscoverIntent.LoadMovies -> loadFirstPage(intent.genreId, intent.genreName)
            DiscoverIntent.LoadNextPage -> loadNextPage()
            DiscoverIntent.Retry -> retry()
        }
    }

    private fun loadFirstPage(genreId: String, genreName: String) {
        _state.update {
            it.copy(
                genreId = genreId,
                genreName = genreName,
                isLoading = true,
                movies = emptyList(),
                currentPage = 1,
                isLastPage = false,
                error = null,
                paginationError = null
            )
        }
        viewModelScope.launch {
            try {
                val movies = getDiscoverMoviesUseCase(genreId, 1)
                _state.update {
                    it.copy(
                        isLoading = false,
                        movies = movies,
                        isLastPage = movies.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.toUiText()
                    )
                }
            }
        }
    }

    private fun loadNextPage() {
        val currentState = _state.value
        val genreId = currentState.genreId ?: return
        if (currentState.isLoading || currentState.isLoadingNextPage || currentState.isLastPage) return

        _state.update { it.copy(isLoadingNextPage = true, paginationError = null) }
        val nextPage = currentState.currentPage + 1

        viewModelScope.launch {
            try {
                val newMovies = getDiscoverMoviesUseCase(genreId, nextPage)
                _state.update {
                    it.copy(
                        isLoadingNextPage = false,
                        currentPage = nextPage,
                        movies = it.movies + newMovies,
                        isLastPage = newMovies.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingNextPage = false,
                        paginationError = e.toUiText()
                    )
                }
            }
        }
    }

    private fun retry() {
        val currentState = _state.value
        val genreId = currentState.genreId ?: return
        val genreName = currentState.genreName

        if (currentState.error != null) {
            loadFirstPage(genreId, genreName)
        } else if (currentState.paginationError != null) {
            loadNextPage()
        }
    }
}
