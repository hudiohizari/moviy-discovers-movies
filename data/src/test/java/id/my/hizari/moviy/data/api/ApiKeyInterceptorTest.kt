/**
 * id.my.hizari.moviy.data.api
 *
 * Created by Hudio Hizari on 09/06/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

package id.my.hizari.moviy.data.api

import io.mockk.*
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiKeyInterceptorTest {

    @Test
    fun intercept_appendsApiKeyQueryParameter() {
        val apiKey = "test_api_key_123"
        val interceptor = ApiKeyInterceptor(apiKey)

        val originalRequest = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/popular")
            .build()

        val chain = mockk<Interceptor.Chain>()
        val mockResponse = mockk<Response>()

        val requestSlot = slot<Request>()
        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(requestSlot)) } returns mockResponse

        val response = interceptor.intercept(chain)

        assertEquals(mockResponse, response)
        val modifiedRequest = requestSlot.captured
        val modifiedUrl = modifiedRequest.url
        assertEquals("test_api_key_123", modifiedUrl.queryParameter("api_key"))
    }
}
