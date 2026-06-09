/**
 * id.my.hizari.moviy.ui.search
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.moviy.R
import id.my.hizari.moviy.domain.usecase.SearchMoviesUseCase
import id.my.hizari.moviy.ui.components.UiText
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(value = SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _queryFlow = MutableStateFlow(value = "")

    init {
        viewModelScope.launch {
            _queryFlow.collect {
                println("VM_DEBUG: raw _queryFlow collected: '$it'")
            }
        }
        viewModelScope.launch {
            _queryFlow
                .debounce(timeoutMillis = 500L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    println("VM_DEBUG: debounced collectLatest query: '$query'")
                    executeFirstPageSearch(query)
                }
        }
    }

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.query) }
                _queryFlow.value = intent.query
            }

            SearchIntent.LoadNextPage -> loadNextPage()
            SearchIntent.Retry -> retry()
        }
    }

    private fun executeFirstPageSearch(query: String) {
        if (query.trim().isEmpty()) {
            _state.update {
                it.copy(
                    isLoading = false,
                    movies = emptyList(),
                    currentPage = 1,
                    isLastPage = true,
                    error = null,
                    paginationError = null
                )
            }
            return
        }

        _state.update {
            it.copy(
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
                val results = searchMoviesUseCase(query = query, page = 1)
                _state.update {
                    it.copy(
                        isLoading = false,
                        movies = results,
                        isLastPage = results.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage?.let { msg -> UiText.DynamicString(value = msg) }
                            ?: UiText.StringResource(resId = R.string.error_unexpected)
                    )
                }
            }
        }
    }

    private fun loadNextPage() {
        val currentState = _state.value
        val query = currentState.query
        if (query.trim()
                .isEmpty() || currentState.isLoading || currentState.isLoadingNextPage || currentState.isLastPage
        ) return

        _state.update { it.copy(isLoadingNextPage = true, paginationError = null) }
        val nextPage = currentState.currentPage + 1

        viewModelScope.launch {
            try {
                val newResults = searchMoviesUseCase(query = query, page = nextPage)
                _state.update {
                    it.copy(
                        isLoadingNextPage = false,
                        currentPage = nextPage,
                        movies = it.movies + newResults,
                        isLastPage = newResults.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingNextPage = false,
                        paginationError = e.localizedMessage?.let { msg ->
                            UiText.DynamicString(
                                value = msg
                            )
                        }
                            ?: UiText.StringResource(resId = R.string.error_unexpected)
                    )
                }
            }
        }
    }

    private fun retry() {
        val currentState = _state.value
        if (currentState.error != null) {
            executeFirstPageSearch(currentState.query)
        } else if (currentState.paginationError != null) {
            loadNextPage()
        }
    }
}
