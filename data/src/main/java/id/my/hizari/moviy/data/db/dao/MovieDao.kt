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
import id.my.hizari.moviy.data.db.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM favorite_movies")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(movie: MovieEntity)

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavoriteMovie(movieId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    fun isFavorite(movieId: Int): Flow<Boolean>

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId")
    suspend fun getFavoriteMovie(movieId: Int): MovieEntity?
}
