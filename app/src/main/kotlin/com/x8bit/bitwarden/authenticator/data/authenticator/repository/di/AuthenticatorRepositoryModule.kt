package com.x8bit.bitwarden.authenticator.data.authenticator.repository.di

import com.x8bit.bitwarden.authenticator.data.authenticator.datasource.disk.AuthenticatorDiskSource
import com.x8bit.bitwarden.authenticator.data.authenticator.manager.TotpCodeManager
import com.x8bit.bitwarden.authenticator.data.authenticator.repository.AuthenticatorRepository
import com.x8bit.bitwarden.authenticator.data.authenticator.repository.AuthenticatorRepositoryImpl
import com.x8bit.bitwarden.authenticator.data.platform.manager.DispatcherManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides repositories in the authenticator package.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthenticatorRepositoryModule {

    @Provides
    @Singleton
    fun provideAuthenticatorRepository(
        authenticatorDiskSource: AuthenticatorDiskSource,
        dispatcherManager: DispatcherManager,
        totpCodeManager: TotpCodeManager,
    ): AuthenticatorRepository = AuthenticatorRepositoryImpl(
        authenticatorDiskSource,
        totpCodeManager,
        dispatcherManager,
    )

}
