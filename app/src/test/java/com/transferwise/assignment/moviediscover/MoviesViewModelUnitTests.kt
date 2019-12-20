package com.transferwise.assignment.moviediscover


import com.airbnb.mvrx.*
import com.transferwise.assignment.moviediscover.data.state.MoviesState
import com.transferwise.assignment.moviediscover.data_source.MoviesDataSource
import com.transferwise.assignment.moviediscover.service_locator.GetMovies
import com.transferwise.assignment.moviediscover.view_model.MoviesViewModel
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.test.inject
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MoviesViewModelUnitTests : BaseTestsImpl<MoviesState, MoviesViewModel>() {


    override val viewModel: MoviesViewModel by lazy {
        MoviesViewModel(MoviesState(), MoviesDataSource(), getMovies,false)
    }

    private fun testLoadingState() {
        verifyStateWithPending {
                it.movieListState is Loading &&
                        it.movieList != null &&
                        it.movieList!!.size == 0 &&
                        it.reloaded.hasValue
        }
    }

    private fun testSuccessfulState(defaultSize: Int = 20) {
        verifyStateWithPending {
                it.movieListState is Success &&
                        it.movieList != null &&
                        it.movieList!!.size == defaultSize &&
                        it.reloaded.value
        }
    }

    private fun testFailedState(listSize: Int = 0) {
        verifyStateWithPending {
            val state = it.movieListState
            it.movieList?.size == listSize &&
                    !it.reloaded.value &&
                    state is Fail &&
                    state.error?.message == "response_error_message"
        }
    }

    private fun testSimpleDataConsistency(expectedSize: Int, loadingIndex: Int?, expectedId: Long,
                                          checkingIndex: Int) {
        verifyStateWithPending {
            if(it.movieList?.size == expectedSize) {
                if(loadingIndex != null)
                    it.movieList?.loadAround(loadingIndex)
                it.movieList?.get(checkingIndex)?.id == expectedId
            } else
                false
        }
    }

    private fun testLoading(response: List<MockResponse>) {
        setupWithTestInfrastructure(response)
        viewModel.requestMoviesList()
        testLoadingState()
    }

    private fun testCompleteState(response: List<MockResponse> = shortSuccessfulResponse) {
        setupWithTestInfrastructure(response)
        viewModel.requestMoviesList()
        testSuccessfulState()
    }

    private inline fun <reified T: Async<Unit>> isCorrectEvent(event: Pair<Async<Unit>, Int>?,
                                                               expectedValue: Int) =
        (event != null && event.first is T && event.second == expectedValue)


    @Test
    fun viewModelInitialState_success() {
        setupKoin("")
        withState(viewModel) {
            assertTrue(
                it.movieList == null &&
                        !it.reloaded.value &&
                        it.movieListState is Uninitialized
            )
        }
    }

    @Test
    fun viewModelLoadingSate_success() {
        setupWithTestInfrastructure(shortSuccessfulResponse)
        viewModel.requestMoviesList()
        testLoadingState()
    }

    @Test
    fun viewModelCompleteState_success() {
        testCompleteState()
    }

    @Test
    fun viewModelOfflineCacheRequest_success() {
        testCompleteState()
        isOnline = false
        viewModel.reloadMoviesList()
        testSuccessfulState()
    }

    @Test
    fun viewModelReloadWithNewData_success() {
        testCompleteState(shortSuccessfulResponse + longSuccessfulResponse)
        verifyStateWithPending {
            it.movieList?.get(0)?.id == 616700L
        }
        viewModel.reloadMoviesList()
        testLoadingState()
        testSuccessfulState()
        verifyStateWithPending {
            it.movieList?.get(0)?.id == 565716L
        }
    }

    @Test
    fun viewModelFailedResponse_fail() {
        setupWithTestInfrastructure(failedResponse)
        viewModel.requestMoviesList()
        testLoadingState()
        testFailedState()
    }

    @Test
    fun viewModelPagination_success() {
        testLoading(longSuccessfulResponse)
        testSimpleDataConsistency(20, 20, 565716, 0)
        testSimpleDataConsistency(40, 40, 624629, 20)
        testSimpleDataConsistency(56, null, 506511, 40)
    }

    @Test
    fun viewModelLoadMissingPages_success() {
        testLoading(longBreakResponse)
        testSimpleDataConsistency(20, 20, 565716, 0)
        testFailedState(20)
        viewModel.retrieveLastPage()
        testSimpleDataConsistency(40, null, 624629, 20)
    }

    @Test
    fun viewModelRequestMoviesList_success() {
        testCompleteState()
        viewModel.requestMoviesList()
        await()
        assertTrue(withState(viewModel) {
                    it.movieListState is Success
                    it.movieList?.size == 20 &&
                    !it.reloaded.value
        })
    }

    @Test
    fun viewModelPagedListWithDataSource_success() {
        setupWithTestInfrastructure(longBreakResponse)
        val dataSource = MoviesDataSource()
        val getMovies: GetMovies by inject()
        val pagedList = getMovies(dataSource)
        val events: Queue<Pair<Async<Unit>, Int>> = LinkedList()
        dataSource.state.observeForever {
            println(it)
            events.add(it)
        }
        await(testDelay)
        pagedList.loadAround(20)
        pagedList.loadAround(40)
        await(testDelay)
        if(dataSource.hasMissingContent)
            dataSource.retrieveMissedPages()
        await(testDelay)
        assertTrue(isCorrectEvent<Loading<Unit>>(events.poll(), 1))
        assertTrue(isCorrectEvent<Success<Unit>>(events.poll(), 1))
        assertTrue(isCorrectEvent<Loading<Unit>>(events.poll(), 2))
        assertTrue(isCorrectEvent<Fail<Unit>>(events.poll(), 2))
        assertTrue(isCorrectEvent<Loading<Unit>>(events.poll(), 2))
        assertTrue(isCorrectEvent<Success<Unit>>(events.poll(), 2))
        assertTrue(isCorrectEvent<Loading<Unit>>(events.poll(), 3))
        assertTrue(isCorrectEvent<Success<Unit>>(events.poll(), 3))
    }

}
