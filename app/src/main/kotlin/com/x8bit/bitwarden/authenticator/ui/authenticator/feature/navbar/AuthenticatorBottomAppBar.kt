package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.navbar

import android.os.Parcelable
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.x8bit.bitwarden.authenticator.R
import com.x8bit.bitwarden.authenticator.ui.authenticator.feature.itemlisting.ITEM_LISTING_GRAPH_ROUTE
import kotlinx.parcelize.Parcelize

@Suppress("LongMethod")
@Composable
fun AuthenticatorBottomAppBar(
    navController: NavController,
    verificationCodesTabClickedAction: () -> Unit,
    settingsTabClickedAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier,
    ) {
        val destinations = listOf(
            AuthenticatorNavBarTab.VerificationCodes,
            AuthenticatorNavBarTab.Settings,
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        destinations.forEach { destination ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == destination.route
            } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (isSelected) {
                                destination.iconResSelected
                            } else {
                                destination.iconRes
                            },
                        ),
                        contentDescription = stringResource(
                            id = destination.contentDescriptionRes,
                        ),
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = destination.labelRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                selected = isSelected,
                onClick = {
                    when (destination) {
                        AuthenticatorNavBarTab.VerificationCodes -> {
                            verificationCodesTabClickedAction()
                        }

                        AuthenticatorNavBarTab.Settings -> {
                            settingsTabClickedAction()
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.semantics { testTag = destination.testTag },
            )
        }
    }
}

/**
 * Represents the different tabs available in the navigation bar
 * for the authenticator screens.
 *
 * Each tab is modeled with properties that provide information on:
 * - Regular icon resource
 * - Icon resource when selected
 * and other essential UI and navigational data.
 *
 * @property iconRes The resource ID for the regular (unselected) icon representing the tab.
 * @property iconResSelected The resource ID for the icon representing the tab when it's selected.
 */
@Parcelize
private sealed class AuthenticatorNavBarTab : Parcelable {
    /**
     * The resource ID for the icon representing the tab when it is selected.
     */
    abstract val iconResSelected: Int

    /**
     * Resource id for the icon representing the tab.
     */
    abstract val iconRes: Int

    /**
     * Resource id for the label describing the tab.
     */
    abstract val labelRes: Int

    /**
     * Resource id for the content description describing the tab.
     */
    abstract val contentDescriptionRes: Int

    /**
     * Route of the tab.
     */
    abstract val route: String

    /**
     * The test tag of the tab.
     */
    abstract val testTag: String

    /**
     * Show the Verification Codes screen.
     */
    @Parcelize
    data object VerificationCodes : AuthenticatorNavBarTab() {
        override val iconResSelected get() = R.drawable.ic_verification_codes_filled
        override val iconRes get() = R.drawable.ic_verification_codes
        override val labelRes get() = R.string.verification_codes
        override val contentDescriptionRes get() = R.string.verification_codes
        override val route get() = ITEM_LISTING_GRAPH_ROUTE
        override val testTag get() = "SettingsTab"
    }

    /**
     * Show the Settings screen.
     */
    @Parcelize
    data object Settings : AuthenticatorNavBarTab() {
        override val iconResSelected get() = R.drawable.ic_settings_filled
        override val iconRes get() = R.drawable.ic_settings
        override val labelRes get() = R.string.settings
        override val contentDescriptionRes get() = R.string.settings
        override val route get() = "settings_graph"
        override val testTag get() = "SettingsTab"
    }
}

/**
 * Helper function to generate [NavOptions] for [VaultUnlockedNavBarScreen].
 */
private fun NavController.vaultUnlockedNavBarScreenNavOptions(): NavOptions =
    navOptions {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
