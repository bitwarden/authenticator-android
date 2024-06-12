package com.bitwarden.authenticator.data.platform.manager.imports.parsers

import android.net.Uri
import com.bitwarden.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemAlgorithm
import com.bitwarden.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemEntity
import com.bitwarden.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemType
import com.bitwarden.authenticator.data.authenticator.manager.TotpCodeManager
import com.bitwarden.authenticator.data.authenticator.manager.model.ExportJsonData
import com.bitwarden.authenticator.data.platform.manager.imports.model.ImportFileFormat
import com.bitwarden.authenticator.data.platform.util.asFailure
import com.bitwarden.authenticator.data.platform.util.asSuccess
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.ByteArrayInputStream
import java.io.IOException

class BitwardenExportParser(
    private val fileFormat: ImportFileFormat,
) : ExportParser {
    override fun parse(byteArray: ByteArray): Result<List<AuthenticatorItemEntity>> {
        return when (fileFormat) {
            ImportFileFormat.BITWARDEN_JSON -> importJsonFile(byteArray)
            else -> IllegalArgumentException("Unsupported file format.").asFailure()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun importJsonFile(byteArray: ByteArray): Result<List<AuthenticatorItemEntity>> {
        val importJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }

        return try {
            importJson
                .decodeFromStream<ExportJsonData>(ByteArrayInputStream(byteArray))
                .asSuccess()
                .mapCatching { exportData ->
                    exportData
                        .items
                        .filter { it.login?.totp != null }
                        .toAuthenticatorItemEntities()
                }
        } catch (e: SerializationException) {
            e.asFailure()
        } catch (e: IllegalArgumentException) {
            e.asFailure()
        } catch (e: IOException) {
            e.asFailure()
        }
    }

    private fun List<ExportJsonData.ExportItem>.toAuthenticatorItemEntities() =
        map { it.toAuthenticatorItemEntity() }

    private fun ExportJsonData.ExportItem.toAuthenticatorItemEntity(): AuthenticatorItemEntity {
        val otpString = requireNotNull(login?.totp)

        val otpUri = when {
            otpString.startsWith(TotpCodeManager.TOTP_CODE_PREFIX) -> {
                Uri.parse(otpString)
            }

            otpString.startsWith(TotpCodeManager.STEAM_CODE_PREFIX) -> {
                Uri.parse(otpString)
            }

            else -> {
                val uriString =
                    "${TotpCodeManager.TOTP_CODE_PREFIX}/$name?${TotpCodeManager.SECRET_PARAM}=$otpString"
                Uri.parse(uriString)
            }
        }

        val type = if (otpUri.scheme == "otpauth" && otpUri.authority == "totp") {
            AuthenticatorItemType.TOTP
        } else if (otpUri.scheme == "steam") {
            AuthenticatorItemType.STEAM
        } else {
            throw IllegalArgumentException("Unsupported OTP type.")
        }

        val key = when (type) {
            AuthenticatorItemType.TOTP -> {
                requireNotNull(otpUri.getQueryParameter(TotpCodeManager.SECRET_PARAM))
            }

            AuthenticatorItemType.STEAM -> {
                requireNotNull(otpUri.authority)
            }
        }

        val algorithm = otpUri.getQueryParameter(TotpCodeManager.ALGORITHM_PARAM)
            ?: TotpCodeManager.ALGORITHM_DEFAULT.name

        val period = otpUri.getQueryParameter(TotpCodeManager.PERIOD_PARAM)
            ?.toIntOrNull()
            ?: TotpCodeManager.PERIOD_SECONDS_DEFAULT

        val digits = when (type) {
            AuthenticatorItemType.TOTP -> {
                otpUri.getQueryParameter(TotpCodeManager.DIGITS_PARAM)
                    ?.toIntOrNull()
                    ?: TotpCodeManager.TOTP_DIGITS_DEFAULT
            }

            AuthenticatorItemType.STEAM -> {
                TotpCodeManager.STEAM_DIGITS_DEFAULT
            }
        }
        val issuer = otpUri.getQueryParameter(TotpCodeManager.ISSUER_PARAM)
            ?: name

        val label = when (type) {
            AuthenticatorItemType.TOTP -> {
                otpUri.pathSegments
                    .firstOrNull()
                    .orEmpty()
                    .removePrefix("$issuer:")
            }

            AuthenticatorItemType.STEAM -> null
        }

        return AuthenticatorItemEntity(
            id = id,
            key = key,
            type = type,
            algorithm = algorithm.let { AuthenticatorItemAlgorithm.valueOf(it) },
            period = period,
            digits = digits,
            issuer = issuer,
            accountName = label,
            favorite = favorite,
        )
    }
}
