package com.bitwarden.authenticator.ui.auth.unlock

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val UNLOCK_ROUTE: String = "unlock"

fun NavController.navigateToUnlock(
    navOptions: NavOptions? = null,
) {
    navigate(route = UNLOCK_ROUTE, navOptions = navOptions)
}

fun NavGraphBuilder.unlockDestination(
    onUnlocked: () -> Unit,
) {
    composable(route = UNLOCK_ROUTE) {
        UnlockScreen(onUnlocked = onUnlocked)
    }
}
