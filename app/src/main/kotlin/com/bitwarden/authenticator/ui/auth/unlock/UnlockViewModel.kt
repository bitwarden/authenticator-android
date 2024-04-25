package com.bitwarden.authenticator.ui.auth.unlock

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bitwarden.authenticator.R
import com.bitwarden.authenticator.data.authenticator.repository.model.UnlockResult
import com.bitwarden.authenticator.data.platform.manager.BiometricsEncryptionManager
import com.bitwarden.authenticator.data.platform.repository.SettingsRepository
import com.bitwarden.authenticator.ui.platform.base.BaseViewModel
import com.bitwarden.authenticator.ui.platform.base.util.Text
import com.bitwarden.authenticator.ui.platform.base.util.asText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

private const val KEY_STATE = "state"

@HiltViewModel
class UnlockViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
    private val biometricsEncryptionManager: BiometricsEncryptionManager,
) : BaseViewModel<UnlockState, UnlockEvent, UnlockAction>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        UnlockState(
            isBiometricsEnabled = settingsRepository.isUnlockWithBiometricsEnabled,
            isBiometricsValid = biometricsEncryptionManager.isBiometricIntegrityValid(),
            dialog = null,
        )
    }
) {

    init {
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: UnlockAction) {
        when (action) {
            UnlockAction.BiometricsUnlock -> {
                handleBiometricsUnlock()
            }

            UnlockAction.DismissDialog -> {
                handleDismissDialog()
            }

            UnlockAction.BiometricsLockout -> {
                handleBiometricsLockout()
            }
        }
    }

    private fun handleBiometricsUnlock() {
        if (state.isBiometricsEnabled && !state.isBiometricsValid) {
            biometricsEncryptionManager.setupBiometrics()
        }
        sendEvent(UnlockEvent.BiometricUnlock)
    }

    private fun handleDismissDialog() {
        mutableStateFlow.update { it.copy(dialog = null) }
    }

    private fun handleBiometricsLockout() {
        mutableStateFlow.update {
            it.copy(
                dialog = UnlockState.Dialog.Error(
                    message = R.string.too_many_failed_biometric_attempts.asText(),
                )
            )
        }
    }
}

@Parcelize
data class UnlockState(
    val isBiometricsEnabled: Boolean,
    val isBiometricsValid: Boolean,
    val dialog: Dialog?,
) : Parcelable {

    @Parcelize
    sealed class Dialog : Parcelable {
        data class Error(
            val message: Text,
        ) : Dialog()

        data object Loading : Dialog()
    }
}

sealed class UnlockEvent {

    data object BiometricUnlock : UnlockEvent()

    data class ShowToast(
        val message: Text,
    ) : UnlockEvent()

}

sealed class UnlockAction {
    data object DismissDialog : UnlockAction()

    data object BiometricsLockout : UnlockAction()

    data object BiometricsUnlock : UnlockAction()

    sealed class Internal {
        data class ReceiveUnlockResult(
            val unlockResult: UnlockResult,
        ) : Internal()
    }
}
