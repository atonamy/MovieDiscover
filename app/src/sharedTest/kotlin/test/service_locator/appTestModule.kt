package test.service_locator

import androidx.paging.PagedList
import com.transferwise.assignment.moviediscover.data_source.MoviesDataSource
import com.transferwise.assignment.moviediscover.errors.ErrorMessages
import com.transferwise.assignment.moviediscover.network.NetworkInfo
import com.transferwise.assignment.moviediscover.network.api.MoviesApi
import com.transferwise.assignment.moviediscover.network.retrofit.ApiBuilder
import com.transferwise.assignment.moviediscover.network.retrofit.interceptors.CacheInterceptor
import com.transferwise.assignment.moviediscover.repositories.MoviesRepository
import com.transferwise.assignment.moviediscover.repositories.MoviesRepositoryImpl
import com.transferwise.assignment.moviediscover.service_locator.GetMovies
import org.koin.dsl.module
import test.mocks.ErrorMessagesMockImpl
import test.mocks.NetworkInfoMockImpl
import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun appTestModule(
    baseUrl: String,
    cacheDir: String,
    notifyExecutor: Executor,
    isOnline: () -> Boolean) = module {
    single<GetMovies> {
        {
            PagedList.Builder(it, MoviesDataSource.config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setNotifyExecutor(notifyExecutor)
                .build()
        }
    }
    single<MoviesRepository> {
        MoviesRepositoryImpl() }
    single<MoviesApi> { ApiBuilder(baseUrl).build() }
    single { CacheInterceptor(cacheDir) }
    single<NetworkInfo> { NetworkInfoMockImpl(isOnline) }
    single<ErrorMessages> { ErrorMessagesMockImpl() }
}