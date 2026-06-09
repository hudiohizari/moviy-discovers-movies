/**
 * id.my.hizari.moviy.domain.usecase
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.usecase

import id.my.hizari.moviy.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Boolean> {
        return repository.isFavorite(movieId = movieId)
    }
}
