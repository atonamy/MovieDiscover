package com.transferwise.assignment.moviediscover.view_model

import com.airbnb.mvrx.*
import com.transferwise.assignment.moviediscover.data.model.MovieDetails
import com.transferwise.assignment.moviediscover.data.state.MovieDetailsState
import com.transferwise.assignment.moviediscover.data.state.SingleEvent
import com.transferwise.assignment.moviediscover.errors.ErrorMessages
import com.transferwise.assignment.moviediscover.extensions.processExceptionForMvRx
import com.transferwise.assignment.moviediscover.repositories.MoviesRepository
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class MovieDetailsViewModel(
    initialState: MovieDetailsState,
    private val moviesRepo: MoviesRepository
) : MvRxViewModel<MovieDetailsState>(initialState), KoinComponent {

    private val errorMessages by inject<ErrorMessages>()


    fun requestMovieDetails(movieId: Long) = withState {
        if(it.movieRequest is Uninitialized)
            loadMovieDetails(movieId)
    }

    fun reloadMovieDetails(movieId: Long) {
        if(moviesRepo.isNetworkAvailable)
            loadMovieDetails(movieId)
    }

    fun requestWebsite() = setState {
        val movie = getMovieDetails(this)
        var result: MovieDetailsState =
            copy(website = setValue(""))
        if(movie != null) {
                val url = movie.homePage
                result = if(!url.isNullOrEmpty())
                    copy(website = setValue(url))
                else
                    copy(error = setValue(errorMessages.noWebsiteErrorMessage))
        }
        result
    }

    fun requestTrailer() = setState {
        val movie = getMovieDetails(this)
        var result: MovieDetailsState =
            copy(trailer = setValue(""))
        if(movie != null) {
            val trailers = movie.videos?.trailers?.filter { it.website == "YouTube" }
            result = if(!trailers.isNullOrEmpty())
                copy(trailer = setValue(
                    trailers[Random().nextInt(trailers.size)].videoUri
                ))
            else
                copy(error = setValue(errorMessages.noTrailerErrorMessage))
        }

        result
    }

    private fun loadMovieDetails(movieId: Long) {
        moviesRepo.getMovieDetails(movieId).execute {
            if(it is Fail)
                copy(movieRequest = it.error.processExceptionForMvRx())
            else
                copy(movieRequest = it)
        }
    }

    private fun getMovieDetails(state: MovieDetailsState): MovieDetails? {
        val movie = state.movieRequest
        return if(movie is Success) movie() else null
    }

    private fun setValue(value: String): SingleEvent<String> =
        SingleEvent(value, "")

    companion object : MvRxViewModelFactory<MovieDetailsViewModel, MovieDetailsState> {

        override fun create(viewModelContext: ViewModelContext, state: MovieDetailsState): MovieDetailsViewModel {
            val moviesRepo by viewModelContext.activity.inject<MoviesRepository>()
            return MovieDetailsViewModel(
                state,
                moviesRepo
            )
        }
    }
}