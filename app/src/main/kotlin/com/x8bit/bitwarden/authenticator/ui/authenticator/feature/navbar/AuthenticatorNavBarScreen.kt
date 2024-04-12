package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.navbar

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.x8bit.bitwarden.authenticator.R
import com.x8bit.bitwarden.authenticator.ui.authenticator.feature.itemlisting.ITEM_LISTING_GRAPH_ROUTE
import com.x8bit.bitwarden.authenticator.ui.authenticator.feature.itemlisting.itemListingGraph
import com.x8bit.bitwarden.authenticator.ui.authenticator.feature.itemlisting.navigateToItemListGraph
import com.x8bit.bitwarden.authenticator.ui.platform.base.util.EventsEffect
import com.x8bit.bitwarden.authenticator.ui.platform.base.util.max
import com.x8bit.bitwarden.authenticator.ui.platform.components.scaffold.BitwardenScaffold
import com.x8bit.bitwarden.authenticator.ui.platform.components.scrim.BitwardenAnimatedScrim
import com.x8bit.bitwarden.authenticator.ui.platform.theme.RootTransitionProviders
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun AuthenticatorNavBarScreen(
    viewModel: AuthenticatorNavBarViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    onNavigateToQrCodeScanner: () -> Unit,
    onNavigateToManualKeyEntry: () -> Unit,
    onNavigateToEditItem: (itemId: String) -> Unit,
) {
    EventsEffect(viewModel = viewModel) { event ->
        navController.apply {
            val navOptions = navController.authenticatorNavBarScreenNavOptions()
            when (event) {
                AuthenticatorNavBarEvent.NavigateToSettings -> {
                    Toast
                        .makeText(
                            navController.context,
                            R.string.not_yet_implemented,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                    /* navigateToSettingGraph() */
                }

                AuthenticatorNavBarEvent.NavigateToVerificationCodes -> {
                    navigateToItemListGraph(navOptions)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        navController
            .currentBackStackEntryFlow
            .onEach {
                viewModel.trySendAction(AuthenticatorNavBarAction.BackStackUpdate)
            }
            .launchIn(this)
    }

    AuthenticatorNavBarScaffold(
        navController = navController,
        verificationTabClickedAction = {
            viewModel.trySendAction(AuthenticatorNavBarAction.VerificationCodesTabClick)
        },
        settingsTabClickedAction = {
            viewModel.trySendAction(AuthenticatorNavBarAction.SettingsTabClick)
        },
        navigateToQrCodeScanner = onNavigateToQrCodeScanner,
        navigateToManualKeyEntry = onNavigateToManualKeyEntry,
        navigateToEditItem = onNavigateToEditItem,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatorNavBarScaffold(
    navController: NavHostController,
    verificationTabClickedAction: () -> Unit,
    settingsTabClickedAction: () -> Unit,
    navigateToQrCodeScanner: () -> Unit,
    navigateToManualKeyEntry: () -> Unit,
    navigateToEditItem: (itemId: String) -> Unit,
) {
    var shouldDimNavBar by remember { mutableStateOf(false) }

    BitwardenScaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars),
        bottomBar = {
            Box {
                var appBarHeightPx by remember { mutableIntStateOf(0) }
                AuthenticatorBottomAppBar(
                    modifier = Modifier
                        .onGloballyPositioned {
                            appBarHeightPx = it.size.height
                        },
                    navController = navController,
                    verificationCodesTabClickedAction = verificationTabClickedAction,
                    settingsTabClickedAction = settingsTabClickedAction,
                )
                BitwardenAnimatedScrim(
                    isVisible = shouldDimNavBar,
                    onClick = {
                        // Do nothing
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(appBarHeightPx.dp)
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ITEM_LISTING_GRAPH_ROUTE,
            modifier = Modifier
                .consumeWindowInsets(WindowInsets.navigationBars)
                .consumeWindowInsets(WindowInsets.ime)
                .padding(innerPadding.max(WindowInsets.ime)),
            enterTransition = RootTransitionProviders.Enter.fadeIn,
            exitTransition = RootTransitionProviders.Exit.fadeOut,
            popEnterTransition = RootTransitionProviders.Enter.fadeIn,
            popExitTransition = RootTransitionProviders.Exit.fadeOut,
        ) {
            itemListingGraph(
                navController = navController,
                navigateToQrCodeScanner = navigateToQrCodeScanner,
                navigateToManualKeyEntry = navigateToManualKeyEntry,
                navigateToEditItem = navigateToEditItem,
            )
        }
    }
}

/**
 * Helper function to generate [NavOptions] for [VaultUnlockedNavBarScreen].
 */
private fun NavController.authenticatorNavBarScreenNavOptions(): NavOptions =
    navOptions {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
