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
import id.my.hizari.moviy.data.datasource.LocalMovieCacheStore
import id.my.hizari.moviy.data.model.AuthorDetailsDto
import id.my.hizari.moviy.data.model.GenreDto
import id.my.hizari.moviy.data.model.GenreResponse
import id.my.hizari.moviy.data.model.MovieDto
import id.my.hizari.moviy.data.model.MovieResponse
import id.my.hizari.moviy.data.model.ReviewDto
import id.my.hizari.moviy.data.model.ReviewResponse
import id.my.hizari.moviy.data.model.VideoDto
import id.my.hizari.moviy.data.model.VideoResponse
import id.my.hizari.moviy.domain.model.Genre
import id.my.hizari.moviy.domain.model.Movie
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieRepositoryImplTest {

    private val api: TmdbApi = mockk()
    private val localGenreStore: LocalGenreStore = mockk(relaxed = true)
    private val localFavoriteStore: LocalFavoriteStore = mockk()
    private val localMovieCacheStore: LocalMovieCacheStore = mockk()

    private val repository = MovieRepositoryImpl(
        api = api,
        localGenreStore = localGenreStore,
        localFavoriteStore = localFavoriteStore,
        localMovieCacheStore = localMovieCacheStore
    )

    @Test
    fun getGenres_remoteSuccess_cachesAndReturnsList() {
        runBlocking {
            val remoteGenres = GenreResponse(
                genres = listOf(
                    GenreDto(1, "Action"),
                    GenreDto(2, "Comedy")
                )
            )
            val expectedGenres = listOf(
                Genre(1, "Action"),
                Genre(2, "Comedy")
            )
            coEvery { api.getGenres() } returns remoteGenres
            coEvery { localGenreStore.saveGenres(any()) } just Runs

            val result = repository.getGenres()

            assertEquals(expectedGenres, result)
            coVerify(exactly = 1) { localGenreStore.saveGenres(expectedGenres) }
        }
    }

    @Test
    fun getGenres_remoteError_fallbackToCachedGenres() {
        runBlocking {
            val cachedGenres = listOf(
                Genre(1, "Action"),
                Genre(2, "Comedy")
            )
            coEvery { api.getGenres() } throws RuntimeException("Network failure")
            coEvery { localGenreStore.getGenres() } returns cachedGenres

            val result = repository.getGenres()

            assertEquals(cachedGenres, result)
        }
    }

    @Test(expected = RuntimeException::class)
    fun getGenres_remoteError_emptyCache_rethrowsException() {
        runBlocking {
            coEvery { api.getGenres() } throws RuntimeException("Network failure")
            coEvery { localGenreStore.getGenres() } returns emptyList()

            repository.getGenres()
        }
    }

    @Test
    fun discoverMovies_success_returnsMappedMovies() {
        runBlocking {
            val movieResponse = MovieResponse(
                page = 1,
                results = listOf(
                    MovieDto(
                        id = 100,
                        title = "Test Movie",
                        overview = "Test Overview",
                        posterPath = "/test.jpg",
                        backdropPath = "/backdrop.jpg",
                        releaseDate = "2026-06-09",
                        voteAverage = 7.8
                    )
                ),
                totalPages = 10,
                totalResults = 100
            )
            val expectedMovies = listOf(
                Movie(
                    id = 100,
                    title = "Test Movie",
                    overview = "Test Overview",
                    posterPath = "/test.jpg",
                    backdropPath = "/backdrop.jpg",
                    releaseDate = "2026-06-09",
                    voteAverage = 7.8
                )
            )
            coEvery { api.discoverMovies("28", 1) } returns movieResponse
            coEvery { localMovieCacheStore.saveMovies("genre_28", 1, expectedMovies) } just Runs

            val result = repository.discoverMovies("28", 1)

            assertEquals(expectedMovies, result)
        }
    }

    @Test
    fun discoverMovies_remoteError_fallbackToCache() {
        runBlocking {
            val cachedMovies = listOf(
                Movie(
                    id = 100,
                    title = "Cached Movie",
                    overview = "Overview",
                    posterPath = null,
                    backdropPath = null,
                    releaseDate = null,
                    voteAverage = 7.0
                )
            )
            coEvery { api.discoverMovies("28", 1) } throws RuntimeException("Network error")
            coEvery { localMovieCacheStore.getMovies("genre_28", 1) } returns cachedMovies

            val result = repository.discoverMovies("28", 1)

            assertEquals(cachedMovies, result)
        }
    }

    @Test
    fun searchMovies_success_returnsMappedMovies() {
        runBlocking {
            val movieResponse = MovieResponse(
                page = 1,
                results = listOf(
                    MovieDto(
                        id = 101,
                        title = "Searched Movie",
                        overview = "Searched Overview",
                        posterPath = "/search.jpg",
                        backdropPath = "/back_search.jpg",
                        releaseDate = "2026-06-09",
                        voteAverage = 8.5
                    )
                ),
                totalPages = 5,
                totalResults = 50
            )
            val expectedMovies = listOf(
                Movie(
                    id = 101,
                    title = "Searched Movie",
                    overview = "Searched Overview",
                    posterPath = "/search.jpg",
                    backdropPath = "/back_search.jpg",
                    releaseDate = "2026-06-09",
                    voteAverage = 8.5
                )
            )
            coEvery { api.searchMovies("Action", 1) } returns movieResponse
            coEvery {
                localMovieCacheStore.saveMovies(
                    "search_Action",
                    1,
                    expectedMovies
                )
            } just Runs
            coEvery { localMovieCacheStore.pruneSearchCache(86400000L) } just Runs

            val result = repository.searchMovies("Action", 1)

            assertEquals(expectedMovies, result)
        }
    }

    @Test
    fun searchMovies_remoteError_fallbackToCache() {
        runBlocking {
            val cachedMovies = listOf(
                Movie(
                    id = 101,
                    title = "Cached Search",
                    overview = "Overview",
                    posterPath = null,
                    backdropPath = null,
                    releaseDate = null,
                    voteAverage = 8.0
                )
            )
            coEvery { api.searchMovies("Action", 1) } throws RuntimeException("Network error")
            coEvery { localMovieCacheStore.getMovies("search_Action", 1) } returns cachedMovies

            val result = repository.searchMovies("Action", 1)

            assertEquals(cachedMovies, result)
        }
    }

    @Test
    fun getMovieDetails_success_returnsMappedMovie() {
        runBlocking {
            val movieDetailDto = MovieDto(
                id = 100,
                title = "Test Movie Detail",
                overview = "Overview Details",
                posterPath = "/detail.jpg",
                backdropPath = "/backdropDetail.jpg",
                releaseDate = "2026-06-09",
                voteAverage = 8.1,
                runtime = 120,
                genres = listOf(GenreDto(1, "Action"))
            )
            val expectedMovie = Movie(
                id = 100,
                title = "Test Movie Detail",
                overview = "Overview Details",
                posterPath = "/detail.jpg",
                backdropPath = "/backdropDetail.jpg",
                releaseDate = "2026-06-09",
                voteAverage = 8.1,
                runtime = 120,
                genres = listOf(Genre(1, "Action"))
            )
            coEvery { api.getMovieDetails(100) } returns movieDetailDto

            val result = repository.getMovieDetails(100)

            assertEquals(expectedMovie, result)
        }
    }

    @Test
    fun getMovieDetails_remoteError_fallbackToFavorites() {
        runBlocking {
            val expectedMovie = Movie(
                id = 100,
                title = "Favorite Movie",
                overview = "Overview",
                posterPath = null,
                backdropPath = null,
                releaseDate = null,
                voteAverage = 8.0
            )
            coEvery { api.getMovieDetails(100) } throws RuntimeException("Network error")
            coEvery { localFavoriteStore.getFavoriteMovie(100) } returns expectedMovie

            val result = repository.getMovieDetails(100)

            assertEquals(expectedMovie, result)
        }
    }

    @Test
    fun getMovieDetails_remoteError_notInFavorites_fallbackToCache() {
        runBlocking {
            val expectedMovie = Movie(
                id = 100,
                title = "Cached Movie Detail",
                overview = "Overview",
                posterPath = null,
                backdropPath = null,
                releaseDate = null,
                voteAverage = 8.0
            )
            coEvery { api.getMovieDetails(100) } throws RuntimeException("Network error")
            coEvery { localFavoriteStore.getFavoriteMovie(100) } returns null
            coEvery { localMovieCacheStore.getMovie(100) } returns expectedMovie

            val result = repository.getMovieDetails(100)

            assertEquals(expectedMovie, result)
        }
    }

    @Test
    fun getMovieReviews_success_returnsMappedReviews() {
        runBlocking {
            val reviewResponse = ReviewResponse(
                id = 100,
                page = 1,
                results = listOf(
                    ReviewDto(
                        id = "rev1",
                        author = "Author Name",
                        authorDetails = AuthorDetailsDto("Name", "Username", null, 9.0),
                        content = "Review Content",
                        createdAt = "2026-06-09T00:00:00Z"
                    )
                ),
                totalPages = 1,
                totalResults = 1
            )
            coEvery { api.getMovieReviews(100, 1) } returns reviewResponse

            val result = repository.getMovieReviews(100, 1)

            assertEquals(1, result.size)
            assertEquals("rev1", result[0].id)
            assertEquals("Author Name", result[0].author)
            assertEquals("Review Content", result[0].content)
        }
    }

    @Test
    fun getMovieTrailers_success_returnsMappedTrailers() {
        runBlocking {
            val videoResponse = VideoResponse(
                id = 100,
                results = listOf(
                    VideoDto(
                        id = "vid1",
                        key = "youtube_key",
                        name = "Trailer 1",
                        site = "YouTube",
                        type = "Trailer"
                    )
                )
            )
            coEvery { api.getMovieVideos(100) } returns videoResponse

            val result = repository.getMovieTrailers(100)

            assertEquals(1, result.size)
            assertEquals("vid1", result[0].id)
            assertEquals("youtube_key", result[0].key)
            assertEquals("Trailer 1", result[0].name)
        }
    }

    @Test
    fun getFavoriteMovies_returnsFlowFromFavoriteStore() {
        runBlocking {
            val favoriteList = listOf(
                Movie(1, "Title 1", "Overview 1", null, null, null, 7.5)
            )
            coEvery { localFavoriteStore.favorites } returns flowOf(favoriteList)

            val flow = repository.getFavoriteMovies()
            val result = flow.first()

            assertEquals(favoriteList, result)
        }
    }

    @Test
    fun toggleFavorite_togglesFavoriteInStore() {
        runBlocking {
            val movie = Movie(1, "Title 1", "Overview 1", null, null, null, 7.5)
            coEvery { localFavoriteStore.toggleFavorite(movie) } returns true

            val result = repository.toggleFavorite(movie)

            assertTrue(result)
            coVerify(exactly = 1) { localFavoriteStore.toggleFavorite(movie) }
        }
    }

    @Test
    fun isFavorite_returnsFlowFromFavoriteStore() {
        runBlocking {
            coEvery { localFavoriteStore.isFavorite(100) } returns flowOf(true)

            val flow = repository.isFavorite(100)
            val result = flow.first()

            assertTrue(result)
        }
    }
}
