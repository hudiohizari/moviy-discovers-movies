/**
 * id.my.hizari.moviy.ui.favorites
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.moviy.R
import id.my.hizari.moviy.domain.usecase.GetFavoriteMoviesUseCase
import id.my.hizari.moviy.ui.components.UiText
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(value = FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        handleIntent(intent = FavoritesIntent.LoadFavorites)
    }

    fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            FavoritesIntent.LoadFavorites -> loadFavorites()
        }
    }

    private fun loadFavorites() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                getFavoriteMoviesUseCase().collect { movies ->
                    _state.update { it.copy(isLoading = false, movies = movies) }
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
}
