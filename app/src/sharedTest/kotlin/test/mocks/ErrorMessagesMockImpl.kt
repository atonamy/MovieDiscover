package test.mocks

import com.transferwise.assignment.moviediscover.errors.ErrorMessages

class ErrorMessagesMockImpl : ErrorMessages {
    override val genericErrorMessage: String
        get() = "generic_error"
    override val noWebsiteErrorMessage: String
        get() = "no_website_error"
    override val noTrailerErrorMessage: String
        get() = "no_trailer_error"

}