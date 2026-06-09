/**
 * id.my.hizari.moviy.ui.genres
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.genres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.moviy.domain.usecase.GetGenresUseCase
import id.my.hizari.moviy.ui.components.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GenreState())
    val state: StateFlow<GenreState> = _state.asStateFlow()

    init {
        handleIntent(GenreIntent.LoadGenres)
    }

    fun handleIntent(intent: GenreIntent) {
        when (intent) {
            GenreIntent.LoadGenres -> loadGenres()
            GenreIntent.Retry -> loadGenres()
        }
    }

    private fun loadGenres() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val genres = getGenresUseCase()
                _state.update { it.copy(isLoading = false, genres = genres) }
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
}
