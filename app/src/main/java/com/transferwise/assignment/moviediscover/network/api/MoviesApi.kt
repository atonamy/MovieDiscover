package com.transferwise.assignment.moviediscover.network.api

import com.transferwise.assignment.moviediscover.Settings
import com.transferwise.assignment.moviediscover.data.model.MovieDetails
import com.transferwise.assignment.moviediscover.data.model.MoviesResponse
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {

    companion object {
        const val defaultSort = "vote_count.desc"
    }

    @GET("discover/movie")
    suspend fun retrieveMoviesInReleaseDate(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = Settings.apiKey,
        @Query("sort_by") sortBy: String = defaultSort,
        @Query("primary_release_date.gte") startReleaseDate: String =
            LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        @Query("primary_release_date.lte") endReleaseDate: String =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    ): Response<MoviesResponse>

    @GET("movie/{id}")
    fun retrieveMovieDetails(
        @Path("id") movieId: Long,
        @Query("api_key") apiKey: String = Settings.apiKey,
        @Query("append_to_response") append: String = "videos"
    ): Single<Response<MovieDetails>>

}