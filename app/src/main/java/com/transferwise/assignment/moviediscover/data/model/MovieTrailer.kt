package com.transferwise.assignment.moviediscover.data.model

import com.squareup.moshi.Json

data class MovieTrailer (
    @Json(name = "id")
    val id: String,

    @Json(name = "iso_639_1")
    val languageCode: String,

    @Json(name = "iso_3166_1")
    val countryCode: String,

    @Json(name = "key")
    val videoUri: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "site")
    val website: String,

    @Json(name = "size")
    val size: Int,

    @Json(name = "type")
    val type: String
)