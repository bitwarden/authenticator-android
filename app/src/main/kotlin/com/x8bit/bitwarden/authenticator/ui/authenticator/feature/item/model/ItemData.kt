package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.item.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents the item data displayed to users.
 *
 * @property issuer Name of the item provider.
 * @property alertThresholdSeconds Threshold, in seconds, at which an Item is considered near
 * expiration.
 * @property totpCodeItemData TOTP data for the account.
 */
@Parcelize
data class ItemData(
    val issuer: String,
    val alertThresholdSeconds: Int,
    val totpCodeItemData: TotpCodeItemData?,
) : Parcelable
