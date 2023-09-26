@file:Suppress("DEPRECATION")

package com.entin.lighttasks.presentation.util

import java.util.regex.Pattern


fun String.isUrlLink(): Boolean {
    val regex = ("((http|https)://)(www.)?"
            + "[a-zA-Z0-9@:%._\\+~#?&//=]"
            + "{2,256}\\.[a-z]"
            + "{2,6}\\b([-a-zA-Z0-9@:%"
            + "._\\+~#?&//=]*)")
    return Pattern.compile(regex).matcher(this).matches()
}
