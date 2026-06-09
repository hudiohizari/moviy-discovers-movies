/**
 * id.my.hizari.moviy.data.db
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import id.my.hizari.moviy.data.db.dao.MovieCacheDao
import id.my.hizari.moviy.data.db.dao.MovieDao
import id.my.hizari.moviy.data.db.entity.MovieCacheEntity
import id.my.hizari.moviy.data.db.entity.MovieEntity
import id.my.hizari.moviy.data.db.entity.PaginatedCacheEntity

@Database(
    entities = [
        MovieEntity::class,
        MovieCacheEntity::class,
        PaginatedCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoviyDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieCacheDao(): MovieCacheDao
}
