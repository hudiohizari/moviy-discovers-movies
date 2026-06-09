/**
 * id.my.hizari.moviy.ui.components
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.components

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun TrailerPlayer(
    modifier: Modifier = Modifier,
    videoId: String
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = context as? Activity

    var isFullscreen by remember { mutableStateOf(false) }
    var exitFullscreenFn by remember { mutableStateOf<(() -> Unit)?>(null) }
    var activeFullscreenView by remember { mutableStateOf<View?>(null) }

    if (isFullscreen) {
        BackHandler {
            exitFullscreenFn?.invoke()
        }
    }

    AndroidView(
        factory = { ctx ->
            YouTubePlayerView(context = ctx).apply {
                enableAutomaticInitialization = false
                lifecycleOwner.lifecycle.addObserver(observer = this)

                addFullscreenListener(object : FullscreenListener {
                    override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                        isFullscreen = true
                        exitFullscreenFn = exitFullscreen
                        activeFullscreenView = fullscreenView

                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                        // Hide system status and navigation bars for immersive experience
                        activity?.window?.let { window ->
                            WindowCompat.setDecorFitsSystemWindows(window, false)
                            WindowInsetsControllerCompat(window, window.decorView).apply {
                                hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            }
                        }

                        // Hide the main content view to prevent rendering conflicts
                        val contentView = activity?.findViewById<ViewGroup>(android.R.id.content)
                        contentView?.visibility = View.GONE

                        // Add the fullscreenView to decorView
                        val decorView = activity?.window?.decorView as? ViewGroup
                        (fullscreenView.parent as? ViewGroup)?.removeView(fullscreenView)
                        decorView?.addView(
                            fullscreenView,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }

                    override fun onExitFullscreen() {
                        isFullscreen = false
                        exitFullscreenFn = null

                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                        // Restore system bars
                        activity?.window?.let { window ->
                            WindowCompat.setDecorFitsSystemWindows(window, true)
                            WindowInsetsControllerCompat(window, window.decorView).apply {
                                show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                            }
                        }

                        // Remove the fullscreenView from decorView
                        val decorView = activity?.window?.decorView as? ViewGroup
                        activeFullscreenView?.let {
                            decorView?.removeView(it)
                        }
                        activeFullscreenView = null

                        // Restore visibility of the main content view
                        val contentView = activity?.findViewById<ViewGroup>(android.R.id.content)
                        contentView?.visibility = View.VISIBLE
                    }
                })

                val options = IFramePlayerOptions.Builder(context = ctx)
                    .origin(origin = "https://${ctx.packageName}")
                    .controls(1) // Enable default YouTube controls
                    .fullscreen(1) // Enable native fullscreen button
                    .build()
                initialize(
                    youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.cueVideo(videoId = videoId, startSeconds = 0f)
                        }
                    },
                    playerOptions = options
                )
            }
        },
        onRelease = { view ->
            lifecycleOwner.lifecycle.removeObserver(observer = view)
            view.release()
        },
        modifier = modifier.fillMaxSize()
    )
}
