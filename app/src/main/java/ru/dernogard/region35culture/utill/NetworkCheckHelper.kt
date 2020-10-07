package ru.dernogard.region35culture.utill

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * The methods in this class are check Internet Connection
 */

class NetworkCheckHelper(val context: Context) {

    @Suppress("DEPRECATION") // getActiveNetworkInfo is deprecated on API 29
    fun checkNetworkStatus(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo != null &&
                    connectivityManager.activeNetworkInfo!!.isConnected
        }
    }

    fun checkInternetAvailable(): Boolean {
        return try {
            InetAddress.getByName("www.google.com").equals("")
        } catch (e: UnknownHostException) {
            false
        }
    }
}