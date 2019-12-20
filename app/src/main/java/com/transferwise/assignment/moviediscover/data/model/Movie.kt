package com.transferwise.assignment.moviediscover.data.model

import com.squareup.moshi.Json

data class Movie (
    @Json(name = "genre_ids")
    val genreIds: List<Int>
) : GenericMovie()