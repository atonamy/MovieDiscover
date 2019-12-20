package com.transferwise.assignment.moviediscover

import android.content.Context
import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.transferwise.assignment.moviediscover.ui.activities.MainActivity
import com.transferwise.assignment.moviediscover.ui.controllers.MoviesController
import org.awaitility.kotlin.await

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import test.BaseTests
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AndroidUiInstrumentedTest : BaseTests() {


    private val appContext: Context
        get() = InstrumentationRegistry.getInstrumentation().context

    @get:Rule
    open val activityRule: ActivityTestRule<MainActivity> =
        ActivityTestRule(
            MainActivity::class.java,
            true,
            false
        )

    private inline fun detailsViewTest(crossinline scope: () -> Boolean) {
        advancedActivityTestScope {
            await.atMost(testDelay, TimeUnit.MILLISECONDS).until {
                activityRule.activity.findViewById<View>(R.id.movieView) != null
            }
            Espresso.onView(ViewMatchers.withId(R.id.body))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        ViewActions.click()
                    )
                )
            await.atMost(testDelay, TimeUnit.MILLISECONDS).until {
                activityRule.activity.findViewById<View>(R.id.loadingView) != null
            }
            await.atMost(testDelay, TimeUnit.MILLISECONDS).until {
                scope()
            }
        }
    }

    private inline fun advancedActivityTestScope(crossinline scope: () -> Unit) {
        activityRule.launchActivity(null)
        scope()
        activityRule.finishActivity()
    }

    private inline fun basicActivityTestScope(crossinline scope: () -> Boolean) {
        advancedActivityTestScope {
            await.atMost(testDelay, TimeUnit.MILLISECONDS).until {
                scope()
            }
        }
    }


    @Test
    fun loadingUi_success() {
        setupWithTestInfrastructure(shortSuccessfulResponse)
        basicActivityTestScope {
            activityRule.activity.findViewById<View>(R.id.skeletonView) != null
        }
    }

    @Test
    fun loadedUi_success() {
        setupWithTestInfrastructure(shortSuccessfulResponse)
        basicActivityTestScope {
            activityRule.activity.findViewById<View>(R.id.movieView) != null
        }
    }

    @Test
    fun loadingUi_failed() {
        setupWithTestInfrastructure(failedResponse)
        basicActivityTestScope {
            activityRule.activity.findViewById<View>(R.id.errorView) != null
        }
    }

    @Test
    fun showDetailsUi_success() {
        setupWithTestInfrastructure(shortSuccessfulResponse + detailedSuccessfulResponse)
        detailsViewTest {
            activityRule.activity.findViewById<View>(R.id.movieDetailsView) != null
        }
    }

    @Test
    fun showDetailsUi_failed() {
        setupWithTestInfrastructure(shortSuccessfulResponse + failedResponse)
        detailsViewTest {
            activityRule.activity.findViewById<View>(R.id.errorView) != null
        }
    }

    override val cacheDir: String
        get() = Environment.getExternalStorageDirectory().absolutePath

    override val defaultExecutor: Executor
        get() = MoviesController.ControllerThreadExecutor()

    override fun getFileFromResources(file: String) =
        appContext.assets.open(file).bufferedReader().use { it.readText() }
}
