package com.abhay.crypto.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(private val backStack: MutableList<NavKey>) {

    fun goBack() {
        backStack.removeLastOrNull()
    }
}
