package com.transferwise.assignment.moviediscover.data.model

import com.squareup.moshi.Json
import org.threeten.bp.LocalDate


abstract class GenericMovie {

    @Json(name = "id")
    var id: Long = 0

    @Json(name = "adult")
    var isForAdult: Boolean = false

    @Json(name = "video")
    var hasVideo: Boolean = false

    @Json(name = "poster_path")
    var posterUri: String? = ""

    @Json(name = "backdrop_path")
    var backdropUri: String? = ""

    @Json(name = "title")
    var title: String = ""

    @Json(name = "original_language")
    var originalLanguage: String = ""

    @Json(name = "original_title")
    var originalTitle: String = ""

    @Json(name = "overview")
    var overview: String = ""

    @Json(name = "popularity")
    var popularity: Double = 0.0

    @Json(name = "release_date")
    var releaseDate: LocalDate = LocalDate.now()

    @Json(name = "vote_average")
    var averageVotes: Float = 0f

    @Json(name = "vote_count")
    var votes: Int = 0
}