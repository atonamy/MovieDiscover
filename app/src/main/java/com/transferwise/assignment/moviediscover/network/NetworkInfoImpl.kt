package com.transferwise.assignment.moviediscover.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat

class NetworkInfoImpl(private val context: Context) : NetworkInfo {

    override val isNetworkAvailable: Boolean
        get() {
            val manager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
            val networkInfo = manager?.activeNetworkInfo
            return (networkInfo != null && networkInfo.isConnected)
        }
}