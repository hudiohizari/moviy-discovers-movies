/**
 * id.my.hizari.moviy.navigation
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.navigation

sealed class Screen(val route: String) {
    object Genres : Screen("genres")
    object Search : Screen("search")
    object Discover :
        Screen("discover/{${NavigationArgs.GENRE_ID}}/{${NavigationArgs.GENRE_NAME}}") {
        fun createRoute(genreId: Int, genreName: String): String {
            return "discover/$genreId/$genreName"
        }
    }
    object MovieDetail : Screen("detail/{${NavigationArgs.MOVIE_ID}}") {
        fun createRoute(movieId: Int): String {
            return "detail/$movieId"
        }
    }
}
