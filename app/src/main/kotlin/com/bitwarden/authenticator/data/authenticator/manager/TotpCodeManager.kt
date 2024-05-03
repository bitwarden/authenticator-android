package com.bitwarden.authenticator.data.authenticator.manager

import com.bitwarden.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemEntity
import com.bitwarden.authenticator.data.authenticator.manager.model.VerificationCodeItem
import com.bitwarden.authenticator.data.platform.repository.model.DataState
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages the flows for getting verification codes.
 */
interface TotpCodeManager {

    /**
     * Flow for getting a DataState with multiple verification code items.
     */
    fun getTotpCodesStateFlow(
        itemList: List<AuthenticatorItemEntity>,
    ): StateFlow<DataState<List<VerificationCodeItem>>>

    /**
     * Flow for getting a DataState with a single verification code item.
     */
    fun getTotpCodeStateFlow(
        item: AuthenticatorItemEntity,
    ): StateFlow<DataState<VerificationCodeItem?>>

    companion object {
        const val ALGORITHM = "algorithm"
        const val DIGITS = "digits"
        const val PERIOD = "period"
        const val SECRET = "secret"
        const val ISSUER = "issuer"
        const val TOTP_CODE_PREFIX = "otpauth://totp"
        const val STEAM_CODE_PREFIX = "steam://"
    }
}
