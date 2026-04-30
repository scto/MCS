package com.scto.mcs.app.ui.activities.main

sealed class MainRoutes(val route: String) {
    object Main : MainRoutes("main")

    object Disclaimer : MainRoutes("t&c")
}
