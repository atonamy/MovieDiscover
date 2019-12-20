package com.transferwise.assignment.moviediscover.view_model

import androidx.lifecycle.Observer
import com.airbnb.mvrx.*
import com.transferwise.assignment.moviediscover.data_source.MoviesDataSource
import com.transferwise.assignment.moviediscover.data.state.MoviesState
import com.transferwise.assignment.moviediscover.data.state.SingleEvent
import com.transferwise.assignment.moviediscover.service_locator.GetMovies
import org.koin.android.ext.android.inject

class MoviesViewModel (
    initialState: MoviesState,
    private val dataSource: MoviesDataSource,
    private val getMovies: GetMovies,
    showOnlyWithPosters: Boolean
) : MvRxViewModel<MoviesState>(initialState) {


    private val dataSourceStateObserver = Observer<Pair<Async<Unit>, Int>> {
        setState {
            copy(movieListState = it.first)
        }
    }

    init {
        dataSource.state.observeForever(dataSourceStateObserver)
        dataSource.showOnlyWithPosters = showOnlyWithPosters
    }

    fun reloadMoviesList() {
        setState {
            copy(
                movieList = getMovies(dataSource),
                movieListState = Uninitialized,
                reloaded = SingleEvent(initValue = true, defaultValue = false)
            )
        }
    }

    fun sortBy(type: MoviesDataSource.SortOption) {
        dataSource.sortType = type
        reloadMoviesList()
    }

    fun requestMoviesList() = withState {
        if(it.movieList == null)
            reloadMoviesList()
    }

    fun retrieveLastPage() {
        dataSource.retrieveMissedPages()
    }

    override fun onCleared() {
        super.onCleared()
        dataSource.state.removeObserver(dataSourceStateObserver)
        dataSource.finish()
    }

    companion object : MvRxViewModelFactory<MoviesViewModel, MoviesState> {
        override fun create(viewModelContext: ViewModelContext, state: MoviesState): MoviesViewModel {
            val getMovies by viewModelContext.activity.inject<GetMovies>()
            return MoviesViewModel(state, MoviesDataSource(), getMovies,true)
        }

    }
}