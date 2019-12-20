package com.transferwise.assignment.moviediscover.extensions

import com.airbnb.mvrx.Fail
import retrofit2.HttpException

fun <T> Throwable.processExceptionForMvRx(): Fail<T> =
    if(this is HttpException) {
        val message = parseErrMessageFromResponse()
        if (message != null)
            Fail(Throwable(message))
        else
            Fail(this)
    }
    else
        Fail(this)