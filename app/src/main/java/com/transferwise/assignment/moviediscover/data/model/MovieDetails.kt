package com.transferwise.assignment.moviediscover.data.model

import com.squareup.moshi.Json

data class MovieDetails (
    @Json(name = "budget")
    val budget: Double,

    @Json(name = "genres")
    val genres: List<Genre>,

    @Json(name = "homepage")
    val homePage: String?,

    @Json(name = "imdb_id")
    val imdbId: String?,

    @Json(name = "production_companies")
    val productionCompanies: List<ProductionCompany>,

    @Json(name = "production_countries")
    val productionCountries: List<ProductionCountry>,

    @Json(name = "revenue")
    val revenue: Double,

    @Json(name = "runtime")
    val duration: Int?,

    @Json(name = "spoken_languages")
    val languages: List<Language>,

    @Json(name = "status")
    val status: String,

    @Json(name = "videos")
    val videos: Video?
) : GenericMovie() {
    data class Genre (
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String
    )

    data class ProductionCompany (
        @Json(name = "id")
        val id: Int,

        @Json(name = "logo_path")
        val logoUri: String?,

        @Json(name = "name")
        val name: String,

        @Json(name = "origin_country")
        val country: String
    )

    data class ProductionCountry (
        @Json(name = "iso_3166_1")
        val code: String,

        @Json(name = "name")
        val name: String
    )

    data class Language (
        @Json(name = "iso_639_1")
        val code: String,

        @Json(name = "name")
        val name: String
    )

    data class Video (
        @Json(name = "results")
        val trailers: List<MovieTrailer>
    )
}