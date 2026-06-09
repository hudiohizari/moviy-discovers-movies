/**
 * id.my.hizari.moviy.data.repository
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.repository

import id.my.hizari.moviy.data.api.TmdbApi
import id.my.hizari.moviy.data.datasource.LocalFavoriteStore
import id.my.hizari.moviy.data.datasource.LocalGenreStore
import id.my.hizari.moviy.data.mapper.toDomain
import id.my.hizari.moviy.domain.model.Genre
import id.my.hizari.moviy.domain.model.Movie
import id.my.hizari.moviy.domain.model.Review
import id.my.hizari.moviy.domain.model.Video
import id.my.hizari.moviy.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    private val localGenreStore: LocalGenreStore,
    private val localFavoriteStore: LocalFavoriteStore
) : MovieRepository {

    override suspend fun getGenres(): List<Genre> {
        return try {
            val response = api.getGenres()
            val domainGenres = response.genres.map { it.toDomain() }
            if (domainGenres.isNotEmpty()) {
                localGenreStore.saveGenres(domainGenres)
            }
            domainGenres
        } catch (e: Exception) {
            val cached = localGenreStore.getGenres()
            cached.ifEmpty { throw e }
        }
    }

    override suspend fun discoverMovies(genreId: String?, page: Int): List<Movie> {
        val response = api.discoverMovies(genreId = genreId, page = page)
        return response.results.map { it.toDomain() }
    }

    override suspend fun getMovieDetails(movieId: Int): Movie {
        val response = api.getMovieDetails(movieId = movieId)
        return response.toDomain()
    }

    override suspend fun getMovieReviews(movieId: Int, page: Int): List<Review> {
        val response = api.getMovieReviews(movieId = movieId, page = page)
        return response.results.map { it.toDomain() }
    }

    override suspend fun getMovieTrailers(movieId: Int): List<Video> {
        val response = api.getMovieVideos(movieId = movieId)
        return response.results.map { it.toDomain() }
    }

    override suspend fun searchMovies(query: String, page: Int): List<Movie> {
        val response = api.searchMovies(query = query, page = page)
        return response.results.map { it.toDomain() }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return localFavoriteStore.favorites
    }

    override suspend fun toggleFavorite(movie: Movie): Boolean {
        return localFavoriteStore.toggleFavorite(movie)
    }

    override fun isFavorite(movieId: Int): Flow<Boolean> {
        return localFavoriteStore.isFavorite(movieId)
    }
}
