/**
 * id.my.hizari.moviy.domain.usecase
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.usecase

import id.my.hizari.moviy.domain.model.Video
import id.my.hizari.moviy.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieTrailersUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): List<Video> {
        return repository.getMovieTrailers(movieId = movieId).filter { video ->
            video.site.equals(other = "YouTube", ignoreCase = true) &&
                    video.type.equals(other = "Trailer", ignoreCase = true)
        }
    }
}
