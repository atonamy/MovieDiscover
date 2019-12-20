package com.transferwise.assignment.moviediscover.errors

import android.content.Context
import com.transferwise.assignment.moviediscover.R

class ErrorMessagesImpl(private val context: Context
) : ErrorMessages {
    override val noWebsiteErrorMessage: String
        get() = context.getString(R.string.error_no_website)
    override val noTrailerErrorMessage: String
        get() = context.getString(R.string.error_no_trailer)
    override val genericErrorMessage: String
        get() = context.getString(R.string.error_message_generic)
}