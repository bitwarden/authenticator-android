package com.bitwarden.authenticator.data.platform.datasource.network.service

import com.bitwarden.authenticator.data.platform.datasource.network.model.ConfigResponseJson

/**
 * Provides an API for querying config endpoints.
 */
interface ConfigService {

    /**
     * Fetch app configuration.
     */
    suspend fun getConfig(): Result<ConfigResponseJson>
}
