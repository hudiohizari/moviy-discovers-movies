/**
 * id.my.hizari.moviy.navigation
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import id.my.hizari.moviy.R
import id.my.hizari.moviy.ui.detail.MovieDetailScreen
import id.my.hizari.moviy.ui.discover.DiscoverScreen
import id.my.hizari.moviy.ui.favorites.FavoritesScreen
import id.my.hizari.moviy.ui.genres.GenreScreen
import id.my.hizari.moviy.ui.search.SearchScreen

fun NavGraphBuilder.discoverGraph(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    navigation(
        startDestination = Screen.Genres.route,
        route = Screen.DiscoverGraph.route
    ) {
        composable(route = Screen.Genres.route) {
            GenreScreen(
                onGenreClick = { genreId, genreName ->
                    navController.navigate(
                        route = Screen.Discover.createRoute(
                            genreId = genreId,
                            genreName = genreName
                        )
                    )
                },
                onSearchClick = {
                    navController.navigate(route = Screen.Search.route)
                },
                modifier = modifier.fillMaxSize()
            )
        }
        composable(
            route = Screen.Discover.route,
            arguments = listOf(
                navArgument(name = NavigationArgs.GENRE_ID) {
                    type = NavType.StringType
                },
                navArgument(name = NavigationArgs.GENRE_NAME) {
                    type = NavType.StringType
                }
            )
        ) {
            DiscoverScreen(
                onMovieClick = { movieId ->
                    navController.navigate(
                        route = Screen.DiscoverMovieDetail.createRoute(
                            movieId = movieId
                        )
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                },
                modifier = modifier.fillMaxSize()
            )
        }
        composable(route = Screen.Search.route) {
            SearchScreen(
                onMovieClick = { movieId ->
                    navController.navigate(
                        route = Screen.DiscoverMovieDetail.createRoute(
                            movieId = movieId
                        )
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                },
                modifier = modifier.fillMaxSize()
            )
        }
        composable(
            route = Screen.DiscoverMovieDetail.route,
            arguments = listOf(
                navArgument(name = NavigationArgs.MOVIE_ID, builder = {
                    type = NavType.IntType
                })
            ),
            content = {
                MovieDetailScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    modifier = modifier.fillMaxSize()
                )
            }
        )
    }
}

fun NavGraphBuilder.favoritesGraph(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    navigation(
        startDestination = Screen.Favorites.route,
        route = Screen.FavoritesGraph.route
    ) {
        composable(route = Screen.Favorites.route) {
            FavoritesScreen(
                modifier = modifier,
                onMovieClick = { movieId ->
                    navController.navigate(
                        route = Screen.FavoritesMovieDetail.createRoute(
                            movieId = movieId
                        )
                    )
                }
            )
        }
        composable(
            route = Screen.FavoritesMovieDetail.route,
            arguments = listOf(
                navArgument(name = NavigationArgs.MOVIE_ID, builder = {
                    type = NavType.IntType
                })
            ),
            content = {
                MovieDetailScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    modifier = modifier.fillMaxSize()
                )
            }
        )
    }
}
