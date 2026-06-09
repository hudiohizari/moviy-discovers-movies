/**
 * id.my.hizari.moviy
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import id.my.hizari.moviy.navigation.NavigationArgs
import id.my.hizari.moviy.navigation.Screen
import id.my.hizari.moviy.ui.components.ApiKeyMissingScreen
import id.my.hizari.moviy.ui.discover.DiscoverScreen
import id.my.hizari.moviy.ui.genres.GenreScreen
import id.my.hizari.moviy.ui.search.SearchScreen
import id.my.hizari.moviy.ui.theme.MoviyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoviyTheme(darkTheme = true, dynamicColor = false) {
                MoviyApp()
            }
        }
    }
}

@Composable
fun MoviyApp(
    modifier: Modifier = Modifier,
) {
    // Intercept rendering and display the setup instructions screen if the API key is not configured
    if (BuildConfig.TMDB_API_KEY.isEmpty()) {
        ApiKeyMissingScreen(modifier = modifier)
        return
    }

    var currentDestination by rememberSaveable { mutableStateOf(value = AppDestinations.DISCOVER) }

    // Pre-calculate custom M3 item colors inside the @Composable scope
    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = Color.Transparent // Removes the bulky M3 selected pill highlight
        )
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(id = it.labelRes)
                        )
                    },
                    label = { Text(stringResource(id = it.labelRes)) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it },
                    colors = itemColors
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    insets = WindowInsets.safeDrawing.only(
                        sides = WindowInsetsSides.Top
                    )
                )
        ) {
            val contentModifier = Modifier.fillMaxSize()

            when (currentDestination) {
                AppDestinations.DISCOVER -> {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Genres.route,
                        modifier = contentModifier
                    ) {
                        composable(Screen.Genres.route) {
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
                                    navController.navigate(Screen.Search.route)
                                },
                                modifier = Modifier.fillMaxSize()
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
                                onMovieClick = { _ ->
                                    // Will navigate to Detail in Phase 4
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        composable(Screen.Search.route) {
                            SearchScreen(
                                onMovieClick = { _ ->
                                    // Will navigate to Detail in Phase 4
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                AppDestinations.FAVORITES -> {
                    Box(
                        modifier = contentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.coming_soon_favorites),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                AppDestinations.PROFILE -> {
                    Box(
                        modifier = contentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.coming_soon_profile),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

enum class AppDestinations(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    DISCOVER(labelRes = R.string.nav_discover, icon = Icons.Default.Movie),
    FAVORITES(labelRes = R.string.nav_favorites, icon = Icons.Default.Favorite),
    PROFILE(labelRes = R.string.nav_profile, icon = Icons.Default.AccountCircle),
}