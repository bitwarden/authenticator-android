package com.bitwarden.authenticator.ui.authenticator.feature.itemlisting.util

import com.bitwarden.authenticator.data.authenticator.manager.model.VerificationCodeItem
import com.bitwarden.authenticator.data.authenticator.repository.model.AuthenticatorItem
import com.bitwarden.authenticator.ui.authenticator.feature.itemlisting.ItemListingState
import com.bitwarden.authenticator.ui.authenticator.feature.itemlisting.model.VerificationCodeDisplayItem

/**
 * Transform a list of [VerificationCodeItem] into [ItemListingState.ViewState].
 */
fun List<VerificationCodeItem>.toViewState(
    alertThresholdSeconds: Int,
): ItemListingState.ViewState =
    if (isEmpty()) {
        ItemListingState.ViewState.NoItems
    } else {
        ItemListingState.ViewState.Content(
            favoriteItems = this
                .filter { it.source is AuthenticatorItem.Source.Local && it.source.isFavorite }
                .map {
                    it.toDisplayItem(alertThresholdSeconds = alertThresholdSeconds)
                },
            itemList = this
                .filter { it.source is AuthenticatorItem.Source.Local && !it.source.isFavorite }
                .map {
                    it.toDisplayItem(alertThresholdSeconds = alertThresholdSeconds)
                },
        )
    }

private fun VerificationCodeItem.toDisplayItem(alertThresholdSeconds: Int) =
    VerificationCodeDisplayItem(
        id = id,
        issuer = issuer,
        label = label,
        timeLeftSeconds = timeLeftSeconds,
        periodSeconds = periodSeconds,
        alertThresholdSeconds = alertThresholdSeconds,
        authCode = code,
        favorite = (source as? AuthenticatorItem.Source.Local)?.isFavorite ?: false,
    )
