package com.entin.lighttasks.presentation.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView

/**
 * Searching query after each character added
 */

inline fun SearchView.onSearchTextChanged(crossinline callback: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String?): Boolean {
            this@onSearchTextChanged.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            callback(newText.orEmpty())
            return true
        }
    })
}

// Hiding keyboard

fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
