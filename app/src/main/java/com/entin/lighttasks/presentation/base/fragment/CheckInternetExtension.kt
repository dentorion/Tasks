package com.entin.lighttasks.presentation.base.fragment

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.entin.lighttasks.R
import com.google.android.material.snackbar.Snackbar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire

fun Fragment.initialInternetConnectionChecking(
    binding: ViewGroup,
    liveConnection: MutableLiveData<Boolean>
) {
    // No Internet Snackbar: Fire
    NoInternetSnackbarFire.Builder(binding, lifecycle).apply {
        snackbarProperties.apply {
            connectionCallback = object : ConnectionCallback { // Optional
                override fun hasActiveConnection(hasActiveConnection: Boolean) {
                    liveConnection.postValue(hasActiveConnection)
                }
            }

            // Optional All
            duration = Snackbar.LENGTH_INDEFINITE
            noInternetConnectionMessage = resources.getString(R.string.no_network_connection)
            onAirplaneModeMessage = resources.getString(R.string.no_network_connection_airplane)
            snackbarActionText = resources.getString(R.string.no_network_connection_settings)
            showActionToDismiss = false
            snackbarDismissActionText = resources.getString(R.string.ok)
        }
    }.build()
}