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
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import id.my.hizari.moviy.navigation.Screen
import id.my.hizari.moviy.navigation.discoverGraph
import id.my.hizari.moviy.navigation.favoritesGraph
import id.my.hizari.moviy.navigation.profileGraph
import id.my.hizari.moviy.ui.components.ApiKeyMissingScreen
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

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = AppDestinations.entries.find {
        it.graphRoute == navBackStackEntry?.destination?.parent?.route
    } ?: AppDestinations.DISCOVER

    val tabHistory = remember { mutableStateListOf<String>(Screen.DiscoverGraph.route) }

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
                    onClick = {
                        val targetRoute = it.graphRoute

                        if (it == currentDestination) {
                            navController.popBackStack(route = it.rootRoute, inclusive = false)
                        } else {
                            tabHistory.remove(element = targetRoute)
                            tabHistory.add(element = targetRoute)

                            navController.navigate(route = targetRoute) {
                                popUpTo(id = navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
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
            NavHost(
                navController = navController,
                startDestination = Screen.DiscoverGraph.route,
                modifier = Modifier.fillMaxSize()
            ) {
                discoverGraph(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController
                )
                favoritesGraph(
                    modifier = Modifier.fillMaxSize()
                )
                profileGraph(
                    modifier = Modifier.fillMaxSize()
                )
            }

            val isAtTabRoot =
                navBackStackEntry?.destination?.id == navBackStackEntry?.destination?.parent?.findStartDestination()?.id
            BackHandler(
                enabled = tabHistory.size > 1 && isAtTabRoot,
                onBack = {
                    val currentTabRoute = tabHistory.removeAt(index = tabHistory.lastIndex)
                    val previousTabRoute = tabHistory.lastOrNull() ?: Screen.DiscoverGraph.route
                    navController.navigate(route = previousTabRoute) {
                        popUpTo(id = navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

enum class AppDestinations(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val graphRoute: String,
    val rootRoute: String,
) {
    DISCOVER(
        labelRes = R.string.nav_discover,
        icon = Icons.Default.Movie,
        graphRoute = Screen.DiscoverGraph.route,
        rootRoute = Screen.Genres.route
    ),
    FAVORITES(
        labelRes = R.string.nav_favorites,
        icon = Icons.Default.Favorite,
        graphRoute = Screen.FavoritesGraph.route,
        rootRoute = Screen.Favorites.route
    ),
    PROFILE(
        labelRes = R.string.nav_profile,
        icon = Icons.Default.AccountCircle,
        graphRoute = Screen.ProfileGraph.route,
        rootRoute = Screen.Profile.route
    ),
}