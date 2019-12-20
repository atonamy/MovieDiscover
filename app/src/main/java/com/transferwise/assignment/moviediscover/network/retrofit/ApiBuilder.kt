package com.transferwise.assignment.moviediscover.network.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.transferwise.assignment.moviediscover.BuildConfig
import com.transferwise.assignment.moviediscover.network.retrofit.adapters.LocalDateAdapter
import com.transferwise.assignment.moviediscover.network.retrofit.interceptors.CacheInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiBuilder(private val baseUrl: String) : KoinComponent {

    companion object {
        val moshi: Moshi
            get() = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(LocalDateAdapter)
                .build()
    }

    private val client: OkHttpClient
        get()  {
            val cacheInterceptor by inject<CacheInterceptor>()
            val builder = OkHttpClient
                .Builder()
                .cache(cacheInterceptor.cache)
                .addInterceptor(cacheInterceptor.offlineCacheInterceptor)
                if(BuildConfig.DEBUG)
                    builder.addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
            return builder.build()
        }

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline fun <reified T> build() =
        retrofit.create(T::class.java)
}