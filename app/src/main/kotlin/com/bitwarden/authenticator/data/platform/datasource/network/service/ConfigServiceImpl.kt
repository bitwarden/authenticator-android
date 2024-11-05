package com.bitwarden.authenticator.data.platform.datasource.network.service

import com.bitwarden.authenticator.data.platform.datasource.network.api.ConfigApi
import com.bitwarden.authenticator.data.platform.datasource.network.model.ConfigResponseJson

class ConfigServiceImpl(private val configApi: ConfigApi) : ConfigService {
    override suspend fun getConfig(): Result<ConfigResponseJson> = configApi.getConfig()
}
