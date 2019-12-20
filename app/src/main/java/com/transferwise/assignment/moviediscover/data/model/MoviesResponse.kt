package com.transferwise.assignment.moviediscover.data.model

import com.squareup.moshi.Json

data class MoviesResponse (
    @Json(name = "page")
    val page: Int,

    @Json(name = "total_results")
    val totalRecords: Int,

    @Json(name = "total_pages")
    val totalPages: Int,

    @Json(name = "results")
    val movies: List<Movie>
)