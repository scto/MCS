package com.rk.activities.main

sealed class MainRoutes(val route: String) {
    object Main : MainRoutes("main")
    object Disclaimer : MainRoutes("t&c")
    
    // Added routes for core components
    object Settings : MainRoutes("settings")
    object Editor : MainRoutes("editor")
    object Terminal : MainRoutes("terminal")
    object SidePanel : MainRoutes("sidepanel")
}
