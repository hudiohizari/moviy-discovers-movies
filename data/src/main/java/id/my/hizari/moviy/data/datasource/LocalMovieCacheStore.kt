/**
 * id.my.hizari.moviy.data.datasource
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.datasource

import androidx.room.withTransaction
import id.my.hizari.moviy.data.db.MoviyDatabase
import id.my.hizari.moviy.data.db.dao.MovieCacheDao
import id.my.hizari.moviy.data.db.entity.MovieCacheEntity
import id.my.hizari.moviy.data.db.entity.PaginatedCacheEntity
import id.my.hizari.moviy.domain.model.Movie
import javax.inject.Inject

class LocalMovieCacheStore @Inject constructor(
    private val database: MoviyDatabase,
    private val movieCacheDao: MovieCacheDao
) {
    suspend fun getMovies(cacheKey: String, page: Int): List<Movie> {
        return movieCacheDao.getMoviesFromCache(cacheKey = cacheKey, page = page).map { entity ->
            Movie(
                id = entity.id,
                title = entity.title,
                overview = entity.overview,
                posterPath = entity.posterPath,
                backdropPath = entity.backdropPath,
                releaseDate = entity.releaseDate,
                voteAverage = entity.voteAverage
            )
        }
    }

    suspend fun saveMovies(cacheKey: String, page: Int, movies: List<Movie>) {
        database.withTransaction {
            movieCacheDao.deletePaginatedCache(cacheKey = cacheKey, page = page)
            
            val movieEntities = movies.map { movie ->
                MovieCacheEntity(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    posterPath = movie.posterPath,
                    backdropPath = movie.backdropPath,
                    releaseDate = movie.releaseDate,
                    voteAverage = movie.voteAverage
                )
            }
            movieCacheDao.insertMovies(movies = movieEntities)

            val cacheEntries = movies.mapIndexed { index, movie ->
                PaginatedCacheEntity(
                    cacheKey = cacheKey,
                    page = page,
                    movieId = movie.id,
                    itemIndex = index
                )
            }
            movieCacheDao.insertPaginatedCache(entries = cacheEntries)
        }
    }

    suspend fun pruneSearchCache(maxAgeMs: Long) {
        database.withTransaction {
            val cutoff = System.currentTimeMillis() - maxAgeMs
            movieCacheDao.pruneOldSearchCache(timestamp = cutoff)
            movieCacheDao.deleteOrphanedMovies()
        }
    }

    suspend fun getMovie(movieId: Int): Movie? {
        return movieCacheDao.getMovie(movieId = movieId)?.let { entity ->
            Movie(
                id = entity.id,
                title = entity.title,
                overview = entity.overview,
                posterPath = entity.posterPath,
                backdropPath = entity.backdropPath,
                releaseDate = entity.releaseDate,
                voteAverage = entity.voteAverage
            )
        }
    }
}
