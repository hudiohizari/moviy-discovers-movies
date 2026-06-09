/**
 * id.my.hizari.moviy.data.datasource
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.datasource

import id.my.hizari.moviy.data.db.dao.MovieDao
import id.my.hizari.moviy.data.mapper.toDomain
import id.my.hizari.moviy.data.mapper.toEntity
import id.my.hizari.moviy.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalFavoriteStore @Inject constructor(
    private val movieDao: MovieDao
) {
    val favorites: Flow<List<Movie>> = movieDao.getFavoriteMovies()
        .map { entities -> entities.map { it.toDomain() } }

    suspend fun toggleFavorite(movie: Movie): Boolean {
        val isFav = movieDao.isFavorite(movie.id).first()
        return if (isFav) {
            movieDao.deleteFavoriteMovie(movie.id)
            false
        } else {
            movieDao.insertFavoriteMovie(movie.toEntity())
            true
        }
    }

    fun isFavorite(movieId: Int): Flow<Boolean> {
        return movieDao.isFavorite(movieId)
    }
}
