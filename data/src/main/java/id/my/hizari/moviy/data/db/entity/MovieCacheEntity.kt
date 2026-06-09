/**
 * id.my.hizari.moviy.data.db.entity
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cached_movies")
data class MovieCacheEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double
)

@Entity(
    tableName = "paginated_cache",
    primaryKeys = ["cacheKey", "page", "movieId"],
    indices = [Index(value = ["movieId"])],
    foreignKeys = [
        ForeignKey(
            entity = MovieCacheEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PaginatedCacheEntity(
    val cacheKey: String,
    val page: Int,
    val movieId: Int,
    val itemIndex: Int,
    val createdAt: Long = System.currentTimeMillis()
)
