package com.transferwise.assignment.moviediscover.data_source

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.airbnb.mvrx.*
import com.transferwise.assignment.moviediscover.data.model.Movie
import com.transferwise.assignment.moviediscover.errors.ErrorMessages
import com.transferwise.assignment.moviediscover.extensions.dequeAll
import com.transferwise.assignment.moviediscover.network.NetworkInfo
import com.transferwise.assignment.moviediscover.network.api.MoviesApi
import com.transferwise.assignment.moviediscover.extensions.processExceptionForMvRx
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

typealias Callback = (Int, List<Movie>, Int) -> Unit

class MoviesDataSource : PageKeyedDataSource<Int, Movie>(), KoinComponent {

    companion object {
        val config by lazy {
            PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .build()
        }


    }

    enum class SortOption(val sortBy: String) {
        SortByRevenueDesc("revenue.desc"),
        SortByVotesAverageDesc("vote_average.desc"),
        SortByDefault(MoviesApi.defaultSort)
    }

    val hasMissingContent: Boolean
        get() = missedPages.isNotEmpty()
    val state: MutableLiveData<Pair<Async<Unit>, Int>> = MutableLiveData()
    var showOnlyWithPosters: Boolean = false

    var sortType: SortOption = SortOption.SortByDefault

    private val networkInfo by inject<NetworkInfo>()
    private val api by inject<MoviesApi>()
    private val errorMessages by inject<ErrorMessages>()
    private val missedPages = ConcurrentLinkedQueue<Pair<Int, Callback>>()
    private val jobs = Hashtable<UUID, Job>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        loadMovies(1) { totalPages, movies, nextPage ->
            callback.onResult(movies, null, if (totalPages >= nextPage) nextPage else null)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        loadMovies(params.key) { totalPages, movies, nextPage ->
            callback.onResult(movies, if (totalPages >= nextPage) nextPage else null)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {

    }

    fun retrieveMissedPages() {
        if(networkInfo.isNetworkAvailable)
            missedPages.dequeAll {
                loadMovies(it.first, it.second)
            }
    }

    fun finish() {
        jobs.keys().toList().forEach {
            jobs[it]?.cancel()
        }
        missedPages.clear()
        jobs.clear()
    }



    private fun loadMovies(page: Int, callback: Callback) {
        val id = UUID.randomUUID()
        val job = CoroutineScope(Dispatchers.IO).launch {
            state.postValue(Pair(Loading(), page))
            try {
                delay(250)
                val response = api.retrieveMoviesInReleaseDate(page, sortBy = sortType.sortBy)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        state.postValue(Pair(Success(Unit), page))
                        callback(
                            body.totalPages,
                            body.movies.filter(::posterFilter),
                            page + 1
                        )
                    } else
                        processError(page, callback, Throwable(errorMessages.genericErrorMessage))
                } else
                    processError(page, callback, HttpException(response))
            } catch (e: Exception) {
                processError(page, callback, e)
            } finally {
                jobs.remove(id)
            }
        }
        jobs[id] = job
    }


    private fun processError(page: Int, callback: Callback, error: Throwable) {
        state.postValue(Pair(error.processExceptionForMvRx(), page))
        missedPages.add(Pair(page, callback))
    }

    private fun posterFilter(movie: Movie) =
        !showOnlyWithPosters ||
                !movie.posterUri.isNullOrEmpty() && showOnlyWithPosters
}