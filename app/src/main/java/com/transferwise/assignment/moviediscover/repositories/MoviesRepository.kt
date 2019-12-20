package com.transferwise.assignment.moviediscover.repositories

import com.transferwise.assignment.moviediscover.data.model.MovieDetails
import io.reactivex.Single

interface MoviesRepository {
    val isNetworkAvailable: Boolean
    fun getMovieDetails(movieId: Long): Single<MovieDetails>
}