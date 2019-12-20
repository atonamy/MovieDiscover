package com.transferwise.assignment.moviediscover.network.retrofit.adapters

import com.squareup.moshi.FromJson
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object LocalDateAdapter {

    @FromJson
    fun fromJson(date: String): LocalDate =
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}