/**
 * id.my.hizari.moviy.data.datasource
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.datasource

import id.my.hizari.moviy.data.db.dao.MovieDao
import id.my.hizari.moviy.data.db.entity.MovieEntity
import id.my.hizari.moviy.domain.model.Movie
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalFavoriteStoreTest {

    private val movieDao: MovieDao = mockk()

    @Test
    fun getFavorites_mapsEntitiesToDomainModels() {
        runBlocking {
            val entities = listOf(
                MovieEntity(
                    id = 1,
                    title = "Title 1",
                    overview = "Overview 1",
                    posterPath = "poster1.jpg",
                    backdropPath = "backdrop1.jpg",
                    releaseDate = "2026-06-09",
                    voteAverage = 7.5
                )
            )
            val expected = listOf(
                Movie(
                    id = 1,
                    title = "Title 1",
                    overview = "Overview 1",
                    posterPath = "poster1.jpg",
                    backdropPath = "backdrop1.jpg",
                    releaseDate = "2026-06-09",
                    voteAverage = 7.5
                )
            )
            every { movieDao.getFavoriteMovies() } returns flowOf(entities)
            val localFavoriteStore = LocalFavoriteStore(movieDao)

            val result = localFavoriteStore.favorites.first()

            assertEquals(expected, result)
        }
    }

    @Test
    fun toggleFavorite_isAlreadyFavorite_deletesFromDatabaseAndReturnsFalse() {
        runBlocking {
            val movie =
                Movie(1, "Title 1", "Overview 1", "poster1.jpg", "backdrop1.jpg", "2026-06-09", 7.5)
            every { movieDao.getFavoriteMovies() } returns flowOf(emptyList())
            every { movieDao.isFavorite(1) } returns flowOf(true)
            coEvery { movieDao.deleteFavoriteMovie(1) } just Runs

            val localFavoriteStore = LocalFavoriteStore(movieDao)
            val result = localFavoriteStore.toggleFavorite(movie)

            assertFalse(result)
            coVerify(exactly = 1) { movieDao.deleteFavoriteMovie(1) }
            coVerify(exactly = 0) { movieDao.insertFavoriteMovie(any()) }
        }
    }

    @Test
    fun toggleFavorite_isNotFavorite_insertsIntoDatabaseAndReturnsTrue() {
        runBlocking {
            val movie =
                Movie(1, "Title 1", "Overview 1", "poster1.jpg", "backdrop1.jpg", "2026-06-09", 7.5)
            val expectedEntity = MovieEntity(
                1,
                "Title 1",
                "Overview 1",
                "poster1.jpg",
                "backdrop1.jpg",
                "2026-06-09",
                7.5
            )
            every { movieDao.getFavoriteMovies() } returns flowOf(emptyList())
            every { movieDao.isFavorite(1) } returns flowOf(false)
            coEvery { movieDao.insertFavoriteMovie(any()) } just Runs

            val localFavoriteStore = LocalFavoriteStore(movieDao)
            val result = localFavoriteStore.toggleFavorite(movie)

            assertTrue(result)
            coVerify(exactly = 0) { movieDao.deleteFavoriteMovie(any()) }
            coVerify(exactly = 1) { movieDao.insertFavoriteMovie(expectedEntity) }
        }
    }

    @Test
    fun isFavorite_returnsFlowFromDao() {
        runBlocking {
            every { movieDao.getFavoriteMovies() } returns flowOf(emptyList())
            every { movieDao.isFavorite(1) } returns flowOf(true)

            val localFavoriteStore = LocalFavoriteStore(movieDao)
            val result = localFavoriteStore.isFavorite(1).first()

            assertTrue(result)
        }
    }
}
