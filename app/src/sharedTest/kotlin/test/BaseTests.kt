package test

import com.transferwise.assignment.moviediscover.service_locator.GetMovies
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import test.service_locator.appTestModule
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.MockResponse
import org.koin.test.inject
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

abstract class BaseTests: AutoCloseKoinTest() {

    companion object {
        const val testDelay = 5000L
        const val serverDelay = 500L
        const val shortDelay = 250L
    }

    private lateinit var baseUrl: String
    private lateinit var server: MockWebServer

    protected var isOnline = true
    protected val getMovies by inject<GetMovies>()


    protected val shortSuccessfulResponse =
        listOf(buildResponse("successful_response.json"))

    protected val longSuccessfulResponse =
        listOf(
            buildResponse("first_page_successful_response.json"),
            buildResponse("second_page_successful_response.json"),
            buildResponse("third_page_successful_response.json")
        )

    protected val longBreakResponse =
        listOf(
            buildResponse("first_page_successful_response.json"),
            buildResponse("failed_response.json", 400),
            buildResponse("second_page_successful_response.json"),
            buildResponse("third_page_successful_response.json")
        )

    protected val failedResponse =
        listOf(buildResponse("failed_response.json", 400))

    protected val detailedSuccessfulResponse =
        listOf(buildResponse("detailed_successful_response.json"))

    protected val detailedNoWebsiteNoTrailerResponse =
        listOf(buildResponse("detailed_no_website_no_trailer_response.json"))


    protected abstract val cacheDir: String
    protected abstract  val defaultExecutor: Executor


    protected fun setupKoin(baseUrl: String = this.baseUrl) {
        stopKoin()
        startKoin {
            modules(appTestModule(baseUrl, cacheDir, defaultExecutor) { isOnline })
        }
    }

    protected fun setupServerWithResponse(responses: List<MockResponse>) {
        closeServer()
        server = MockWebServer()
        for(response in responses)
            server.enqueue(response)
        server.start()
        baseUrl = server.url("/").toString()
    }

    protected fun setupWithTestInfrastructure(responses: List<MockResponse>) {
        setupServerWithResponse(responses)
        setupKoin()
    }

    protected fun closeServer() {
        if(::server.isInitialized   )
            server.shutdown()
    }

    protected abstract fun getFileFromResources(file: String): String

    private fun buildResponse(fileBody: String, code: Int = 200) =
        MockResponse()
            .setBodyDelay(serverDelay, TimeUnit.MILLISECONDS)
            .setResponseCode(code)
            .setBody(getFileFromResources(fileBody))
}