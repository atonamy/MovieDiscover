package com.transferwise.assignment.moviediscover


import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import com.transferwise.assignment.moviediscover.service_locator.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : MultiDexApplication() {

    private lateinit var broadcaster: LocalBroadcastManager
    private lateinit var googleApi: GoogleApiAvailability

    override fun onCreate() {
        super.onCreate()
        initServiceLocator()
        upgradeSecurityProvider()
        initFresco()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun initServiceLocator() {
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

    private fun initFresco() {

        Fresco.initialize(
            this,
            ImagePipelineConfig
                .newBuilder(this)
                .setDownsampleEnabled(true)
                .build()
        )
    }



    private fun checkPlayServices(): Boolean {
        googleApi = GoogleApiAvailability.getInstance()
        val result = googleApi.isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            broadcastIntent(result)
            return false
        }

        return true
    }

    private fun upgradeSecurityProvider() {
        broadcaster = LocalBroadcastManager.getInstance(this)
        if(checkPlayServices())
            ProviderInstaller.installIfNeededAsync(this, object : ProviderInstaller.ProviderInstallListener {
                override fun onProviderInstalled() {

                }

                override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent) {
                    broadcastIntent(errorCode)
                }
            })
    }

    private inline fun resolveGoogleApiError(errorCode: Int, resolved: () -> Unit) {
        if(googleApi.isUserResolvableError(errorCode))
            resolved()
    }

    private fun broadcastIntent(errorCode: Int) {
        resolveGoogleApiError(errorCode) {
            Handler().postDelayed({
                broadcaster.sendBroadcast(Intent("gms_data").apply {
                    putExtras(bundleOf("error_code" to errorCode))
                })
            }, 500)
        }
    }
}