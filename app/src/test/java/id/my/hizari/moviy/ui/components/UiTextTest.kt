package id.my.hizari.moviy.ui.components

import id.my.hizari.moviy.R
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UiTextTest {

    @Test
    fun toUiText_unknownHostException_returnsNoInternet() {
        val exception = UnknownHostException("Unable to resolve host")
        val result = exception.toUiText()
        
        assertEquals(
            UiText.StringResource(R.string.error_no_internet),
            result
        )
    }

    @Test
    fun toUiText_connectException_returnsNoInternet() {
        val exception = ConnectException("Connection refused")
        val result = exception.toUiText()
        
        assertEquals(
            UiText.StringResource(R.string.error_no_internet),
            result
        )
    }

    @Test
    fun toUiText_socketTimeoutException_returnsConnectionTimeout() {
        val exception = SocketTimeoutException("Read timed out")
        val result = exception.toUiText()
        
        assertEquals(
            UiText.StringResource(R.string.error_connection_timeout),
            result
        )
    }

    @Test
    fun toUiText_ioException_returnsNoInternet() {
        val exception = IOException("Disk read failure or network error")
        val result = exception.toUiText()
        
        assertEquals(
            UiText.StringResource(R.string.error_no_internet),
            result
        )
    }

    @Test
    fun toUiText_unexpectedException_returnsDynamicString() {
        val exception = IllegalArgumentException("Invalid argument")
        val result = exception.toUiText()
        
        assertEquals(
            UiText.DynamicString("Invalid argument"),
            result
        )
    }

    @Test
    fun toUiText_unexpectedExceptionWithoutMessage_returnsUnexpectedError() {
        val exception = NullPointerException()
        val result = exception.toUiText()
        
        assertEquals(
            UiText.StringResource(R.string.error_unexpected),
            result
        )
    }

    @Test
    fun toUiText_retrofitHttpException_returnsUnexpectedError() {
        val exception = retrofit2.HttpException()
        val result = exception.toUiText()
        
        assertEquals(
            UiText.StringResource(R.string.error_unexpected),
            result
        )
    }
}
