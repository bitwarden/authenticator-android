package com.bitwarden.authenticator.data.authenticator.repository.model

/**
 * Models result of unlocking the vault.
 */
sealed class UnlockResult {

    /**
     * Vault successfully unlocked.
     */
    data object Success : UnlockResult()

    /**
     * Incorrect password provided.
     */
    data object AuthenticationError : UnlockResult()

    /**
     * Unable to access user state information.
     */
    data object InvalidStateError : UnlockResult()

    /**
     * Generic error thrown by Bitwarden SDK.
     */
    data object GenericError : UnlockResult()
}
