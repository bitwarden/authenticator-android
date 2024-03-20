package com.x8bit.bitwarden.authenticator.data.auth.repository

import android.os.SystemClock
import com.x8bit.bitwarden.authenticator.data.auth.datasource.disk.AuthDiskSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDiskSource: AuthDiskSource,
    private val elapsedRealtimeMillisProvider: () -> Long = { SystemClock.elapsedRealtime() },
) : AuthRepository {

    /**
     * Updates the "last active time" for the current user.
     */
    override fun updateLastActiveTime() {
        authDiskSource.storeLastActiveTimeMillis(
            lastActiveTimeMillis = elapsedRealtimeMillisProvider(),
        )
    }
}
