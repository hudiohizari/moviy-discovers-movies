/**
 * id.my.hizari.moviy.data.datasource
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.my.hizari.moviy.domain.model.Genre
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LocalGenreStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) {
    suspend fun getGenres(): List<Genre> {
        return try {
            val preferences = dataStore.data.first()
            val json = preferences[KEY_GENRES] ?: return emptyList()
            val type = object : TypeToken<List<Genre>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveGenres(genres: List<Genre>) {
        val json = gson.toJson(genres)
        dataStore.edit { preferences ->
            preferences[KEY_GENRES] = json
        }
    }

    companion object {
        private val KEY_GENRES = stringPreferencesKey("cached_genres")
    }
}
