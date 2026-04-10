package com.scto.mcs.core.navigation

import androidx.navigation.NavHostController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
    private var navController: NavHostController? = null

    fun setNavController(controller: NavHostController) {
        navController = controller
    }

    fun navigateTo(route: String) {
        navController?.navigate(route)
    }

    fun navigateToEditor(projectPath: String) {
        // Encode path to avoid issues with slashes in route
        val encodedPath = projectPath.replace("/", "|")
        navController?.navigate("editor/$encodedPath")
    }

    fun popBackStack() {
        navController?.popBackStack()
    }
}
