package com.transferwise.assignment.moviediscover

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.withState
import com.transferwise.assignment.moviediscover.view_model.MvRxViewModel
import org.awaitility.kotlin.await
import org.junit.After
import org.junit.Rule
import test.BaseTests
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class BaseTestsImpl<S: MvRxState, T: MvRxViewModel<S>> : BaseTests() {

    @get:Rule
    open val testRule = InstantTaskExecutorRule()


    @After
    fun close() {
        closeServer()
    }

    override val cacheDir by lazy {
        System.getProperty("java.io.tmpdir")
    }

    override val defaultExecutor: Executor
        get() = Executors.newSingleThreadExecutor()

    override fun getFileFromResources(file: String): String {
        val classLoader = javaClass.classLoader
        val resource = classLoader!!.getResource(file)
        return String(File(resource.path).readBytes())
    }

    protected inline fun verifyStateWithPending(awaitMili: Long = testDelay,
                                                crossinline condition: (S) -> Boolean) {

        await.atMost(awaitMili, TimeUnit.MILLISECONDS).until {
            withState(viewModel) { condition(it) }
        }
    }

    abstract protected val viewModel: T

    protected fun await(delay: Long = shortDelay) = Thread.sleep(delay)

}