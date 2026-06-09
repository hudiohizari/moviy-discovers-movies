/**
 * id.my.hizari.moviy.ui.components
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.ui.components

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(@StringRes val resId: Int, vararg val args: Any) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UiText) return false
        return when (this) {
            is DynamicString -> other is DynamicString && value == other.value
            is StringResource -> other is StringResource && resId == other.resId && args.contentEquals(other.args)
        }
    }

    override fun hashCode(): Int {
        return when (this) {
            is DynamicString -> value.hashCode()
            is StringResource -> 31 * resId + args.contentHashCode()
        }
    }
}

fun Throwable.toUiText(): UiText {
    return when (this) {
        is java.net.UnknownHostException -> {
            UiText.StringResource(resId = id.my.hizari.moviy.R.string.error_no_internet)
        }
        is java.net.ConnectException -> {
            UiText.StringResource(resId = id.my.hizari.moviy.R.string.error_no_internet)
        }
        is java.net.SocketTimeoutException -> {
            UiText.StringResource(resId = id.my.hizari.moviy.R.string.error_connection_timeout)
        }
        is java.io.IOException -> {
            UiText.StringResource(resId = id.my.hizari.moviy.R.string.error_no_internet)
        }
        else -> {
            if (this::class.java.name == "retrofit2.HttpException") {
                UiText.StringResource(resId = id.my.hizari.moviy.R.string.error_unexpected)
            } else {
                this.localizedMessage?.let { UiText.DynamicString(value = it) }
                    ?: UiText.StringResource(resId = id.my.hizari.moviy.R.string.error_unexpected)
            }
        }
    }
}
