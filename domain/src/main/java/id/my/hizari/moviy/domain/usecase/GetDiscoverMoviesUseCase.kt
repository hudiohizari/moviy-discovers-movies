/**
 * id.my.hizari.moviy.domain.usecase
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.usecase

import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.repository.MovieRepository
import javax.inject.Inject

class GetDiscoverMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(genreId: String?, page: Int): List<Movie> {
        return repository.discoverMovies(genreId, page)
    }
}
