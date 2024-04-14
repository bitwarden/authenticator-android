package com.x8bit.bitwarden.authenticator.ui.platform.feature.tutorial

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.x8bit.bitwarden.authenticator.ui.authenticator.feature.authenticator.navigateToAuthenticatorGraph

const val TUTORIAL_ROUTE = "tutorial"

fun NavGraphBuilder.tutorialDestination(navController: NavController) {
    composable(TUTORIAL_ROUTE) {
        TutorialScreen(
            onNavigateToAuthenticator = { navController.navigateToAuthenticatorGraph() },
        )
    }
}

fun NavController.navigateToTutorial(navOptions: NavOptions? = null) {
    navigate(route = TUTORIAL_ROUTE, navOptions = navOptions)
}
