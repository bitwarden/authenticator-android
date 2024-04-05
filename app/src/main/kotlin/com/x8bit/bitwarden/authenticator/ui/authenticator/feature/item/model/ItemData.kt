package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.item.model

import android.os.Parcelable
import com.x8bit.bitwarden.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemType
import com.x8bit.bitwarden.authenticator.ui.platform.base.util.Text
import kotlinx.parcelize.Parcelize

/**
 * Represents the item data displayed to users.
 *
 * @property issuer Name of the item provider.
 * @property alertThresholdSeconds Threshold, in seconds, at which an Item is considered near
 * expiration.
 * @property totpCodeItemData TOTP data for the account.
 */
data class ItemData(
    val type: AuthenticatorItemType,
    val username: Text?,
    val issuer: Text,
    val alertThresholdSeconds: Int,
    val totpCodeItemData: TotpCodeItemData?,
)