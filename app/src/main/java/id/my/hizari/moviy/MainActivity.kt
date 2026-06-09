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
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
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
import dagger.hilt.android.AndroidEntryPoint
import id.my.hizari.moviy.ui.components.ApiKeyMissingScreen
import id.my.hizari.moviy.ui.genres.GenreScreen
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

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DISCOVER) }

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
                            contentDescription = stringResource(it.labelRes)
                        )
                    },
                    label = { Text(stringResource(it.labelRes)) },
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
                .consumeWindowInsets(WindowInsets.safeDrawing)
        ) {
            val contentModifier = Modifier.fillMaxSize()

            when (currentDestination) {
                AppDestinations.DISCOVER -> {
                    GenreScreen(
                        onGenreClick = { _, _ ->
                            // Will navigate to Discover Screen in Phase 3
                        },
                        modifier = contentModifier
                    )
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
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    DISCOVER(R.string.nav_discover, Icons.Default.Movie),
    FAVORITES(R.string.nav_favorites, Icons.Default.Favorite),
    PROFILE(R.string.nav_profile, Icons.Default.AccountCircle),
}