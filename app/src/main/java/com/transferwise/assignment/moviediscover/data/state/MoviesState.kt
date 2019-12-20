package com.transferwise.assignment.moviediscover.data.state

import androidx.paging.PagedList
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.transferwise.assignment.moviediscover.data.model.Movie

data class MoviesState (
    val movieListState: Async<Unit> = Uninitialized,
    val movieList: PagedList<Movie>? = null,
    val reloaded: SingleEvent<Boolean> = SingleEvent(initValue = false, defaultValue = false)
) : MvRxState {
    val canRefresh: Boolean
        get() = !movieList.isNullOrEmpty() ||
                movieListState is Fail
}