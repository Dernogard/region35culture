package ru.dernogard.region35culture.listeners

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import ru.dernogard.region35culture.NetworkCheckHelper
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.api.CultureInternetApi

/**
 * Listener for button used for reloading data from Internet
 */

class ReloadDataButtonListener(
    context: Context,
    private val api: CultureInternetApi
) : View.OnClickListener {

    private val netHelp = NetworkCheckHelper(context)
    private lateinit var viewForSnackbar: View

    override fun onClick(p0: View?) {
        viewForSnackbar = p0 ?: return

        if (netHelp.checkNetworkStatus()) {
            if (netHelp.checkInternetAvailable()) {
                // reload data from Internet
                api.getDataAndSaveIt()
            } else {
                showSnackbarMessage(R.string.no_internet_connection)
            }
        } else {
            showSnackbarMessage(R.string.no_network_connection)
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        Snackbar.make(viewForSnackbar, message, Snackbar.LENGTH_SHORT).show()
    }

}