package test.mocks

import com.transferwise.assignment.moviediscover.network.NetworkInfo

class NetworkInfoMockImpl(private val isOnline: () -> Boolean) : NetworkInfo {

    override val isNetworkAvailable: Boolean
        get() = isOnline()
}