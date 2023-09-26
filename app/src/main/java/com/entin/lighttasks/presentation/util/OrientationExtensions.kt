package com.entin.lighttasks.presentation.util

import android.content.Context
import android.content.res.Configuration

fun getCurrentOrientation(context: Context?): Int? =
    context?.resources?.configuration?.orientation

fun isOrientationLandscape(context: Context?): Boolean =
    getCurrentOrientation(context) == Configuration.ORIENTATION_LANDSCAPE
