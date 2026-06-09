/**
 * id.my.hizari.moviy.domain.repository
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.domain.repository

import id.my.hizari.moviy.domain.model.Genre
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.model.Review
import id.my.hizari.moviy.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun getGenres(): List<Genre>

    suspend fun discoverMovies(genreId: String?, page: Int): List<Movie>

    suspend fun getMovieDetails(movieId: Int): Movie

    suspend fun getMovieReviews(movieId: Int, page: Int): List<Review>

    suspend fun getMovieTrailers(movieId: Int): List<Video>

    suspend fun searchMovies(query: String, page: Int): List<Movie>

    fun getFavoriteMovies(): Flow<List<Movie>>

    suspend fun toggleFavorite(movie: Movie): Boolean

    fun isFavorite(movieId: Int): Flow<Boolean>
}
