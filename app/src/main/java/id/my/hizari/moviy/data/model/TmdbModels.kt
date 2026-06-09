/**
 * id.my.hizari.moviy.data.model
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.model

import com.google.gson.annotations.SerializedName

// Genres Response DTOs
data class GenreResponse(
    @SerializedName("genres") val genres: List<Genre>
)

data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

// Movies Discovery Response DTOs
data class MovieResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<Movie>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("runtime") val runtime: Int? = null,
    @SerializedName("genres") val genres: List<Genre>? = null
)

// Movie Reviews Response DTOs
data class ReviewResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<Review>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("author") val author: String,
    @SerializedName("author_details") val authorDetails: AuthorDetails?,
    @SerializedName("content") val content: String,
    @SerializedName("created_at") val createdAt: String?
)

data class AuthorDetails(
    @SerializedName("name") val name: String?,
    @SerializedName("username") val username: String,
    @SerializedName("avatar_path") val avatarPath: String?,
    @SerializedName("rating") val rating: Double?
)

// Videos/Trailers Response DTOs
data class VideoResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: List<Video>
)

data class Video(
    @SerializedName("id") val id: String,
    @SerializedName("key") val key: String, // Youtube video key
    @SerializedName("name") val name: String,
    @SerializedName("site") val site: String, // e.g. "YouTube"
    @SerializedName("type") val type: String // e.g. "Trailer", "Teaser"
)
