package app.bsc.db.drawing.data.network

import android.content.Context
import android.net.ConnectivityManager

/**
 * The purpose of this class is to verify whether or not the phone is connected to the Internet
 */
class NetworkConnectionChecker {

    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting;
        }
    }
}