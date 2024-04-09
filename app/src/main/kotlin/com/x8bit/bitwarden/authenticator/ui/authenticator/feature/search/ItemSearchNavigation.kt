package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.x8bit.bitwarden.authenticator.ui.platform.base.util.composableWithSlideTransitions

const val ITEM_SEARCH_ROUTE = "item_search"

fun NavGraphBuilder.itemSearchDestination(
    onNavigateBack: () -> Unit,
) {
    composableWithSlideTransitions(
        route = ITEM_SEARCH_ROUTE,
    ) {
        ItemSearchScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToSearch() {
    navigate(route = ITEM_SEARCH_ROUTE)
}
