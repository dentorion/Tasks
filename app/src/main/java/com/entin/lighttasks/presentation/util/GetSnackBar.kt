package com.entin.lighttasks.presentation.util

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun getSnackBar(str: String, view: View, length: Int = Snackbar.LENGTH_SHORT): Snackbar {
    return Snackbar
        .make(view, str, length)
        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
}