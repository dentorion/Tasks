package com.example.tasksexample.presentation.util

import androidx.appcompat.widget.SearchView

inline fun SearchView.onSearchTextChanged(crossinline callback: (String) -> Unit) {

    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

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