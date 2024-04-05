package com.x8bit.bitwarden.authenticator.data.authenticator.repository.model

import com.x8bit.bitwarden.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemType

/**
 * Models a request to modify an existing authenticator item.
 *
 * @property type Type of authenticator item.
 * @property period Time, in seconds, the authenticator item verification code is valid. Default is
 * 30 seconds.
 * @property digits Number of digits contained in the verification code for this authenticator item.
 * Default is 6 digits.
 * @property key Key used to generate verification codes for the authenticator item.
 * @property issuer Entity that provided the authenticator item.
 * @property label Optional label for this authenticator item.
 */
data class UpdateItemRequest(
    val type: AuthenticatorItemType,
    val period: Int = 30,
    val digits: Int = 6,
    val key: String,
    val issuer: String?,
    val label: String?,
)
