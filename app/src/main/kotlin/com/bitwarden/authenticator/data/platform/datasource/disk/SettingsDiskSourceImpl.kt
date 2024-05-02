package com.bitwarden.authenticator.data.platform.datasource.disk

import android.content.SharedPreferences
import com.bitwarden.authenticator.data.platform.datasource.disk.BaseDiskSource.Companion.BASE_KEY
import com.bitwarden.authenticator.data.platform.repository.util.bufferedMutableSharedFlow
import com.bitwarden.authenticator.ui.platform.feature.settings.appearance.model.AppLanguage
import com.bitwarden.authenticator.ui.platform.feature.settings.appearance.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onSubscription

private const val APP_THEME_KEY = "$BASE_KEY:theme"
private const val APP_LANGUAGE_KEY = "$BASE_KEY:appLocale"
private const val SYSTEM_BIOMETRIC_INTEGRITY_SOURCE_KEY = "$BASE_KEY:biometricIntegritySource"
private const val ACCOUNT_BIOMETRIC_INTEGRITY_VALID_KEY = "$BASE_KEY:accountBiometricIntegrityValid"
private const val ALERT_THRESHOLD_SECONDS_KEY = "$BASE_KEY:alertThresholdSeconds"
private const val FIRST_LAUNCH_KEY = "$BASE_KEY:hasSeenWelcomeTutorial"
private const val CRASH_LOGGING_ENABLED_KEY = "$BASE_KEY:crashLoggingEnabled"

/**
 * Primary implementation of [SettingsDiskSource].
 */
class SettingsDiskSourceImpl(
    sharedPreferences: SharedPreferences,
) : BaseDiskSource(sharedPreferences = sharedPreferences),
    SettingsDiskSource {
    private val mutableAppThemeFlow =
        bufferedMutableSharedFlow<AppTheme>(replay = 1)

    private val mutableScreenCaptureAllowedFlowMap =
        mutableMapOf<String, MutableSharedFlow<Boolean?>>()

    private val mutableAlertThresholdSecondsFlow =
        bufferedMutableSharedFlow<Int>()

    private val mutableIsCrashLoggingEnabledFlow =
        bufferedMutableSharedFlow<Boolean?>()

    override var appLanguage: AppLanguage?
        get() = getString(key = APP_LANGUAGE_KEY)
            ?.let { storedValue ->
                AppLanguage.entries.firstOrNull { storedValue == it.localeName }
            }
        set(value) {
            putString(
                key = APP_LANGUAGE_KEY,
                value = value?.localeName,
            )
        }

    private val mutableFirstLaunchFlow =
        bufferedMutableSharedFlow<Boolean>()

    override var appTheme: AppTheme
        get() = getString(key = APP_THEME_KEY)
            ?.let { storedValue ->
                AppTheme.entries.firstOrNull { storedValue == it.value }
            }
            ?: AppTheme.DEFAULT
        set(newValue) {
            putString(
                key = APP_THEME_KEY,
                value = newValue.value,
            )
            mutableAppThemeFlow.tryEmit(appTheme)
        }

    override val appThemeFlow: Flow<AppTheme>
        get() = mutableAppThemeFlow
            .onSubscription { emit(appTheme) }

    override var systemBiometricIntegritySource: String?
        get() = getString(key = SYSTEM_BIOMETRIC_INTEGRITY_SOURCE_KEY)
        set(value) {
            putString(key = SYSTEM_BIOMETRIC_INTEGRITY_SOURCE_KEY, value = value)
        }

    override var hasSeenWelcomeTutorial: Boolean
        get() = getBoolean(key = FIRST_LAUNCH_KEY) ?: false
        set(value) {
            putBoolean(key = FIRST_LAUNCH_KEY, value)
            mutableFirstLaunchFlow.tryEmit(hasSeenWelcomeTutorial)
        }

    override val hasSeenWelcomeTutorialFlow: Flow<Boolean>
        get() = mutableFirstLaunchFlow.onSubscription { emit(hasSeenWelcomeTutorial) }

    override var isCrashLoggingEnabled: Boolean?
        get() = getBoolean(key = CRASH_LOGGING_ENABLED_KEY)
        set(value) {
            putBoolean(key = CRASH_LOGGING_ENABLED_KEY, value = value)
            mutableIsCrashLoggingEnabledFlow.tryEmit(value)
        }

    override val isCrashLoggingEnabledFlow: Flow<Boolean?>
        get() = mutableIsCrashLoggingEnabledFlow
            .onSubscription { emit(getBoolean(CRASH_LOGGING_ENABLED_KEY)) }

    override fun storeAlertThresholdSeconds(thresholdSeconds: Int) {
        putInt(
            ALERT_THRESHOLD_SECONDS_KEY,
            thresholdSeconds
        )
        mutableAlertThresholdSecondsFlow.tryEmit(thresholdSeconds)
    }

    override fun getAlertThresholdSeconds(): Int {
        return getInt(ALERT_THRESHOLD_SECONDS_KEY, default = 7) ?: 7
    }

    override fun getAlertThresholdSecondsFlow(): Flow<Int> = mutableAlertThresholdSecondsFlow
        .onSubscription { emit(getAlertThresholdSeconds()) }

    override fun getAccountBiometricIntegrityValidity(
        systemBioIntegrityState: String,
    ): Boolean? =
        getBoolean(
            key = "${ACCOUNT_BIOMETRIC_INTEGRITY_VALID_KEY}_$systemBioIntegrityState",
        )

    override fun storeAccountBiometricIntegrityValidity(
        systemBioIntegrityState: String,
        value: Boolean?,
    ) {
        putBoolean(
            key = "${ACCOUNT_BIOMETRIC_INTEGRITY_VALID_KEY}_$systemBioIntegrityState",
            value = value,
        )
    }
}
