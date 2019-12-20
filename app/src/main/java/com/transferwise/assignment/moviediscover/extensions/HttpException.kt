package com.transferwise.assignment.moviediscover.extensions

import com.squareup.moshi.JsonDataException
import com.transferwise.assignment.moviediscover.data.model.ErrorResponse
import com.transferwise.assignment.moviediscover.network.retrofit.ApiBuilder
import retrofit2.HttpException

fun HttpException.parseErrMessageFromResponse(): String?
{
    val moshi = ApiBuilder.moshi
    val parser = moshi.adapter(ErrorResponse::class.java)
    val response = this.response()?.errorBody()?.string()
    if(response != null)
        try {
            return parser.fromJson(response)?.message
        } catch(e: JsonDataException) {
            e.printStackTrace()
        }
    return null
}