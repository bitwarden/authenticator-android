package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.navbar

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.x8bit.bitwarden.authenticator.ui.platform.base.util.composableWithStayTransitions

const val AUTHENTICATOR_NAV_BAR_ROUTE: String = "AuthenticatorNavBarRoute"

fun NavController.navigateToAuthenticatorNavBar(navOptions: NavOptions? = null) {
    navigate(AUTHENTICATOR_NAV_BAR_ROUTE, navOptions)
}

fun NavGraphBuilder.authenticatorNavBarDestination(
    onNavigateToQrCodeScanner: () -> Unit,
    onNavigateToManualKeyEntry: () -> Unit,
    onNavigateToEditItem: (itemId: String) -> Unit
) {
    composableWithStayTransitions(
        route = AUTHENTICATOR_NAV_BAR_ROUTE,
    ) {
        AuthenticatorNavBarScreen(
            onNavigateToQrCodeScanner = onNavigateToQrCodeScanner,
            onNavigateToManualKeyEntry = onNavigateToManualKeyEntry,
            onNavigateToEditItem = onNavigateToEditItem,
        )
    }
}
