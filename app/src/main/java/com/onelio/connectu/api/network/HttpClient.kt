package com.onelio.connectu.api.network

import android.content.Context
import com.onelio.connectu.App
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit




@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HttpClient {
    private inner class UserAgentInterceptor(private val userAgent: String) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder().header("User-Agent", userAgent).build()
            return chain.proceed(requestWithUserAgent)
        }
    }

    private val userAgent: String = UserAgentSwitcher.Get()
    private var client: OkHttpClient? = null

    private fun initEnv(app: App) {
        if (app.cookieJar == null) {
            app.cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(app.baseContext))
        }
        val builder = OkHttpClient().newBuilder().cookieJar(app.cookieJar)
        builder.connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
        builder.addInterceptor(UserAgentInterceptor(this.userAgent))
        client = builder.build()
    }

    fun HttpClient(context: Context) {
        val app = context.applicationContext as App
        initEnv(app)
    }

    // Normal get
    @Throws(IOException::class)
    operator fun get(url: String, callback: Callback): Call {
        val request = Request.Builder().url(url).addHeader("X-Requested-With", "es.ua.uacloud").build()
        val call = client!!.newCall(request)
        call.enqueue(callback)
        return call
    }

    // Normal post
    @Throws(IOException::class)
    fun post(url: String, fdata: String, callback: Callback): Call {
        val mediaType = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(mediaType, fdata)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build()
        val call = client!!.newCall(request)
        call.enqueue(callback)
        return call
    }

    // JSON post
    @Throws(IOException::class)
    fun jpost(url: String, json: String, callback: Callback): Call {
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(mediaType, json)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build()
        val call = client!!.newCall(request)
        call.enqueue(callback)
        return call
    }

    // Multipart post
    @Throws(IOException::class)
    fun mpost(url: String, body: RequestBody, callback: Callback): Call {
        val request = Request.Builder()
                .url(url)
                .method("POST", RequestBody.create(null, ByteArray(0)))
                .post(body)
                .build()
        val call = client!!.newCall(request)
        call.enqueue(callback)
        return call
    }

    // Normal Download
    @Throws(IOException::class)
    fun download(url: String, callback: Callback): Call {
        val request = Request.Builder().url(url).build()
        val call = client!!.newCall(request)
        call.enqueue(callback)
        return call
    }
}