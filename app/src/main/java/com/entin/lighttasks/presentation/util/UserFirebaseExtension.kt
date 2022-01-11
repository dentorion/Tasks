package com.entin.lighttasks.presentation.util

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Get userUid
 */
fun getUserUid(): String =
    Firebase.auth.currentUser?.uid ?: "0"

/**
 * Check the user is logged in
 */
fun isUserLoggedIn(): Boolean =
    Firebase.auth.currentUser != null

/**
 * User's name
 */
fun getUserName(): String? =
    Firebase.auth.currentUser?.displayName