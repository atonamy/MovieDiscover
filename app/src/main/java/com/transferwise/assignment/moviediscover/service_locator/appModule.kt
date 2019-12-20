package com.transferwise.assignment.moviediscover.service_locator

import androidx.paging.PagedList
import com.transferwise.assignment.moviediscover.Settings
import com.transferwise.assignment.moviediscover.data.model.Movie
import com.transferwise.assignment.moviediscover.data_source.MoviesDataSource
import com.transferwise.assignment.moviediscover.errors.ErrorMessages
import com.transferwise.assignment.moviediscover.errors.ErrorMessagesImpl
import com.transferwise.assignment.moviediscover.network.NetworkInfo
import com.transferwise.assignment.moviediscover.network.NetworkInfoImpl
import com.transferwise.assignment.moviediscover.network.api.MoviesApi
import com.transferwise.assignment.moviediscover.network.retrofit.ApiBuilder
import com.transferwise.assignment.moviediscover.network.retrofit.interceptors.CacheInterceptor
import com.transferwise.assignment.moviediscover.repositories.MoviesRepository
import com.transferwise.assignment.moviediscover.repositories.MoviesRepositoryImpl
import com.transferwise.assignment.moviediscover.ui.controllers.MoviesController
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.Executors

typealias GetMovies = (MoviesDataSource) -> PagedList<Movie>

val appModule = module {
    single<GetMovies> {
        {
            PagedList.Builder(it, MoviesDataSource.config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setNotifyExecutor(MoviesController.ControllerThreadExecutor()
                )
                .build()
        }
    }
    single<MoviesRepository> { MoviesRepositoryImpl() }
    single<MoviesApi> { ApiBuilder(Settings.baseUrl).build() }
    single { CacheInterceptor(androidContext().cacheDir.absolutePath) }
    single<NetworkInfo> { NetworkInfoImpl(androidContext()) }
    single<ErrorMessages> { ErrorMessagesImpl(androidContext()) }
}