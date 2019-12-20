package com.transferwise.assignment.moviediscover.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.airbnb.mvrx.BaseMvRxActivity
import com.google.android.gms.common.GoogleApiAvailability

abstract class BaseActivity: BaseMvRxActivity() {

    private lateinit var broadcaster: LocalBroadcastManager

    private val gmsErrorsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.hasExtra("error_code")) {
                val googleApi = GoogleApiAvailability.getInstance()
                val errorDialog = googleApi.getErrorDialog(
                    this@BaseActivity,
                    intent.getIntExtra("error_code", -1),
                    9000
                )
                errorDialog.setOnDismissListener {
                    finish()
                }
                errorDialog.show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onStart() {
        super.onStart()
        broadcaster.registerReceiver(
            gmsErrorsReceiver,
            IntentFilter("gms_data")
        )
    }

    override fun onStop() {
        super.onStop()
        broadcaster.unregisterReceiver(gmsErrorsReceiver)
    }
}