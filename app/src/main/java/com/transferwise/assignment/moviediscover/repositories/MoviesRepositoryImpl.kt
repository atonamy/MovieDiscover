package com.transferwise.assignment.moviediscover.repositories


import com.transferwise.assignment.moviediscover.data.model.MovieDetails
import com.transferwise.assignment.moviediscover.network.NetworkInfo
import com.transferwise.assignment.moviediscover.network.api.MoviesApi
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException


class MoviesRepositoryImpl : MoviesRepository, KoinComponent {

    private val api by inject<MoviesApi>()
    private val networkInfo by inject<NetworkInfo>()

    override val isNetworkAvailable: Boolean
        get() = networkInfo.isNetworkAvailable


    override fun getMovieDetails(movieId: Long): Single<MovieDetails>  =
        api.retrieveMovieDetails(movieId).map {
            if(it.isSuccessful)
                it.body()
            else
                throw HttpException(it)
        }
}