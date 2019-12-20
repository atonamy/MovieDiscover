package com.transferwise.assignment.moviediscover.data.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.transferwise.assignment.moviediscover.data.model.MovieDetails

data class MovieDetailsState (
    val movieRequest: Async<MovieDetails> = Uninitialized,
    val website: SingleEvent<String> = SingleEvent("", ""),
    val trailer: SingleEvent<String> = SingleEvent("", ""),
    val error: SingleEvent<String> = SingleEvent("", "")
) : MvRxState {
    val showContent: Boolean
        get() = !website.hasValue &&
                !trailer.hasValue &&
                !error.hasValue
}