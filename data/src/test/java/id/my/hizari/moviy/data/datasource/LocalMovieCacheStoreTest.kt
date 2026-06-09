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
import id.my.hizari.moviy.domain.model.Movie
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocalMovieCacheStoreTest {

    private val database: MoviyDatabase = mockk()
    private val movieCacheDao: MovieCacheDao = mockk()
    private lateinit var localMovieCacheStore: LocalMovieCacheStore

    @Before
    fun setUp() {
        localMovieCacheStore = LocalMovieCacheStore(database, movieCacheDao)

        mockkStatic("androidx.room.RoomDatabaseKt")
        coEvery {
            database.withTransaction<Any>(any())
        } coAnswers {
            val block = secondArg<suspend () -> Any>()
            block()
        }
    }

    @Test
    fun getMovies_returnsMappedMovies() {
        runBlocking {
            val entities = listOf(
                MovieCacheEntity(
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
            coEvery { movieCacheDao.getMoviesFromCache("key", 1) } returns entities

            val result = localMovieCacheStore.getMovies("key", 1)

            assertEquals(expected, result)
        }
    }

    @Test
    fun getMovie_returnsMappedMovie() {
        runBlocking {
            val entity = MovieCacheEntity(
                id = 1,
                title = "Title 1",
                overview = "Overview 1",
                posterPath = "poster1.jpg",
                backdropPath = "backdrop1.jpg",
                releaseDate = "2026-06-09",
                voteAverage = 7.5
            )
            val expected = Movie(
                id = 1,
                title = "Title 1",
                overview = "Overview 1",
                posterPath = "poster1.jpg",
                backdropPath = "backdrop1.jpg",
                releaseDate = "2026-06-09",
                voteAverage = 7.5
            )
            coEvery { movieCacheDao.getMovie(1) } returns entity

            val result = localMovieCacheStore.getMovie(1)

            assertEquals(expected, result)
        }
    }

    @Test
    fun getMovie_notFound_returnsNull() {
        runBlocking {
            coEvery { movieCacheDao.getMovie(1) } returns null

            val result = localMovieCacheStore.getMovie(1)

            assertTrue(result == null)
        }
    }

    @Test
    fun saveMovies_executesInTransactionAndInsertsData() {
        runBlocking {
            val movies = listOf(
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
            coEvery { movieCacheDao.deletePaginatedCache("key", 1) } just Runs
            coEvery { movieCacheDao.insertMovies(any()) } just Runs
            coEvery { movieCacheDao.insertPaginatedCache(any()) } just Runs

            localMovieCacheStore.saveMovies("key", 1, movies)

            coVerify(exactly = 1) { movieCacheDao.deletePaginatedCache("key", 1) }
            coVerify(exactly = 1) { movieCacheDao.insertMovies(any()) }
            coVerify(exactly = 1) { movieCacheDao.insertPaginatedCache(any()) }
        }
    }

    @Test
    fun pruneSearchCache_executesPruningAndOrphanCleanup() {
        runBlocking {
            coEvery { movieCacheDao.pruneOldSearchCache(any()) } just Runs
            coEvery { movieCacheDao.deleteOrphanedMovies() } just Runs

            localMovieCacheStore.pruneSearchCache(1000L)

            coVerify(exactly = 1) { movieCacheDao.pruneOldSearchCache(any()) }
            coVerify(exactly = 1) { movieCacheDao.deleteOrphanedMovies() }
        }
    }
}
