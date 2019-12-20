package com.transferwise.assignment.moviediscover

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.transferwise.assignment.moviediscover.data.state.MovieDetailsState
import com.transferwise.assignment.moviediscover.repositories.MoviesRepository
import com.transferwise.assignment.moviediscover.view_model.MovieDetailsViewModel
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.koin.test.inject


class MovieDetailsViewModelUnitTests : BaseTestsImpl<MovieDetailsState, MovieDetailsViewModel>() {

    override val viewModel: MovieDetailsViewModel by lazy {
        val moviesRepo by inject<MoviesRepository>()
        MovieDetailsViewModel(MovieDetailsState(), moviesRepo)
    }

    private fun testLoadingState() {
        verifyStateWithPending {
            it.movieRequest is Loading &&
                    it.showContent &&
                    it.error.value.isEmpty() &&
                    it.trailer.value.isEmpty() &&
                    it.website.value.isEmpty()
        }
    }

    private fun testSuccessfulState() {
        verifyStateWithPending {
            it.movieRequest is Success &&
                    it.showContent &&
                    it.error.value.isEmpty() &&
                    it.trailer.value.isEmpty() &&
                    it.website.value.isEmpty()
        }
    }

    private fun testRequestMovieDetails(response: List<MockResponse> = detailedSuccessfulResponse) {
        setupWithTestInfrastructure(response)
        viewModel.requestMovieDetails(0)
        testLoadingState()
        testSuccessfulState()
        viewModel.requestMovieDetails(0)
        await()
        testSuccessfulState()
    }

    @Test
    fun viewModelInitialState_success() {
        setupKoin("")
        verifyStateWithPending {
            it.showContent &&
                    it.movieRequest is Uninitialized &&
                    it.error.value.isEmpty() &&
                    it.trailer.value.isEmpty() &&
                    it.website.value.isEmpty()
        }
    }

    @Test
    fun viewModelRequestMovieDetails_success() {
        testRequestMovieDetails()
    }

    @Test
    fun viewModelReloadMovieDetails_success() {
        setupWithTestInfrastructure(detailedSuccessfulResponse)
        viewModel.reloadMovieDetails(0)
        testLoadingState()
        testSuccessfulState()
        isOnline = false
        viewModel.reloadMovieDetails(0)
        await()
        testSuccessfulState()
    }

    @Test
    fun viewModelRequestWebsiteAndTrailer_success() {
        testRequestMovieDetails()
        viewModel.requestWebsite()
        viewModel.requestTrailer()
        verifyStateWithPending {
            !it.showContent &&
                    it.error.value.isEmpty()
                    it.website.value.isNotEmpty() &&
                    it.trailer.value.isNotEmpty()
        }
    }

    @Test
    fun viewModelRequestWebsiteAndTrailer_failed() {
        testRequestMovieDetails(detailedNoWebsiteNoTrailerResponse)
        viewModel.requestWebsite()
        viewModel.requestTrailer()
        verifyStateWithPending {
            !it.showContent &&
                    it.error.value.isNotEmpty()
                    it.website.value.isEmpty() &&
                    it.trailer.value.isEmpty()
        }
    }

    @Test
    fun viewModelRequestMovieDetails_failed() {
        setupWithTestInfrastructure(failedResponse)
        viewModel.requestMovieDetails(0)
        testLoadingState()
        verifyStateWithPending {
            val request = it.movieRequest
            it.showContent &&
                    request is Fail &&
                    request.error?.message == "response_error_message" &&
                    it.error.value.isEmpty() &&
                    it.trailer.value.isEmpty() &&
                    it.website.value.isEmpty()
        }

    }
}