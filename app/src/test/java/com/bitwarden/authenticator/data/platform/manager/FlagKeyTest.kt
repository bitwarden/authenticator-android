package com.bitwarden.authenticator.data.platform.manager

import com.bitwarden.authenticator.data.platform.manager.model.FlagKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FlagKeyTest {
    @Test
    fun `Feature flags have the correct key name set`() {
        assertEquals(
            FlagKey.BitwardenAuthenticationEnabled.keyName,
            "bitwarden-authentication-enabled",
        )
        assertEquals(
            FlagKey.PasswordManagerSync.keyName,
            "enable-pm-bwa-sync",
        )
    }
}
