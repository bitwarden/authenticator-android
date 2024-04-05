package com.x8bit.bitwarden.authenticator.data.authenticator.repository.model

import com.x8bit.bitwarden.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemType

data class UpdateItemRequest(
    val type: AuthenticatorItemType,
    val period: Int,
    val digits: Int,
    val key: String,
    val issuer: String,
    val label: String,
)
