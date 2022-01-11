package com.entin.lighttasks.presentation.base.fragment

/**
 * Interface for screens that need an Internet connection
 */

interface InternetDependable {

    /**
     * What should be shown / hidden/ done in case of Internet is / not available
     */
    fun connectionReaction(value: Boolean)
}