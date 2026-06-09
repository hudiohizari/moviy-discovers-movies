/**
 * id.my.hizari.moviy.data.datasource
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import id.my.hizari.moviy.domain.model.Genre
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LocalGenreStoreTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var dataStore: DataStore<Preferences>
    private val gson = Gson()
    private lateinit var localGenreStore: LocalGenreStore

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(tmpFolder.newFolder(), "test.preferences_pb") }
        )
        localGenreStore = LocalGenreStore(dataStore, gson)
    }

    @Test
    fun getGenres_initiallyEmpty_returnsEmptyList() {
        runBlocking {
            val result = localGenreStore.getGenres()
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun saveGenres_savesDataCorrectly_andGetGenresReturnsIt() {
        runBlocking {
            val genres = listOf(
                Genre(1, "Action"),
                Genre(2, "Comedy")
            )

            localGenreStore.saveGenres(genres)

            val result = localGenreStore.getGenres()
            assertEquals(genres, result)
        }
    }
}
