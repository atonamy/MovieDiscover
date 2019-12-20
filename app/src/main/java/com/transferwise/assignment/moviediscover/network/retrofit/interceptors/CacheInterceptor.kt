package com.transferwise.assignment.moviediscover.network.retrofit.interceptors

import android.util.Log
import com.transferwise.assignment.moviediscover.network.NetworkInfo
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.util.concurrent.TimeUnit

class CacheInterceptor(private val cacheDir: String) : KoinComponent {

    companion object {
        private const val headerCacheControl = "Cache-Control"
        private const val httpCache = "http-cache"
        private const val headerPragma = "Pragma"
        private const val cacheSize = 15728640L
        private const val cacheExpirationInHours = 720
    }

    private val networkInfo by inject<NetworkInfo>()


    val cache: Cache?
        get() {
            var cache: Cache? = null
            try {
                cache = Cache(File(cacheDir, httpCache), cacheSize)
            } catch (e: Exception) {
                Log.e(CacheInterceptor::class.simpleName, "Couldn't create cache!")
            }

            return cache
        }

    val offlineCacheInterceptor: Interceptor
        get() = Interceptor { chain ->

            var request = chain.request()

            if (!networkInfo.isNetworkAvailable) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(cacheExpirationInHours, TimeUnit.HOURS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(headerPragma)
                    .removeHeader(headerCacheControl)
                    .cacheControl(cacheControl)
                    .build()
            }

            chain.proceed(request)
        }
}