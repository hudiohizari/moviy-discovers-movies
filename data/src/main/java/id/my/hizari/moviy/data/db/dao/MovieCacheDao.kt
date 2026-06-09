/**
 * id.my.hizari.moviy.data.db.dao
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import id.my.hizari.moviy.data.db.entity.MovieCacheEntity
import id.my.hizari.moviy.data.db.entity.PaginatedCacheEntity

@Dao
interface MovieCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieCacheEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaginatedCache(entries: List<PaginatedCacheEntity>)

    @Query("DELETE FROM paginated_cache WHERE cacheKey = :cacheKey AND page = :page")
    suspend fun deletePaginatedCache(cacheKey: String, page: Int)

    @Transaction
    @Query("""
        SELECT m.* FROM cached_movies m
        INNER JOIN paginated_cache p ON m.id = p.movieId
        WHERE p.cacheKey = :cacheKey AND p.page = :page
        ORDER BY p.itemIndex ASC
    """)
    suspend fun getMoviesFromCache(cacheKey: String, page: Int): List<MovieCacheEntity>

    @Query("DELETE FROM paginated_cache WHERE cacheKey LIKE 'search_%' AND createdAt < :timestamp")
    suspend fun pruneOldSearchCache(timestamp: Long)

    @Query("DELETE FROM cached_movies WHERE id NOT IN (SELECT movieId FROM paginated_cache)")
    suspend fun deleteOrphanedMovies()

    @Query("SELECT * FROM cached_movies WHERE id = :movieId")
    suspend fun getMovie(movieId: Int): MovieCacheEntity?
}
