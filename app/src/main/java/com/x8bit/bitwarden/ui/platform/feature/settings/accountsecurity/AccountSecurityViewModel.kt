package com.x8bit.bitwarden.ui.platform.feature.settings.accountsecurity

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.x8bit.bitwarden.R
import com.x8bit.bitwarden.data.auth.repository.AuthRepository
import com.x8bit.bitwarden.ui.platform.base.BaseViewModel
import com.x8bit.bitwarden.ui.platform.base.util.Text
import com.x8bit.bitwarden.ui.platform.base.util.asText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

private const val KEY_STATE = "state"

/**
 * View model for the account security screen.
 */
@Suppress("TooManyFunctions")
@HiltViewModel
class AccountSecurityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AccountSecurityState, AccountSecurityEvent, AccountSecurityAction>(
    initialState = savedStateHandle[KEY_STATE]
        ?: AccountSecurityState(
            dialog = null,
            isApproveLoginRequestsEnabled = false,
            isUnlockWithBiometricsEnabled = false,
            isUnlockWithPinEnabled = false,
            sessionTimeout = "15 Minutes".asText(),
            sessionTimeoutAction = SessionTimeoutAction.LOCK,
        ),
) {

    init {
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: AccountSecurityAction): Unit = when (action) {
        AccountSecurityAction.AccountFingerprintPhraseClick -> handleAccountFingerprintPhraseClick()
        AccountSecurityAction.BackClick -> handleBackClick()
        AccountSecurityAction.ChangeMasterPasswordClick -> handleChangeMasterPasswordClick()
        AccountSecurityAction.ConfirmLogoutClick -> handleConfirmLogoutClick()
        AccountSecurityAction.DeleteAccountClick -> handleDeleteAccountClick()
        AccountSecurityAction.DismissDialog -> handleDismissDialog()
        AccountSecurityAction.LockNowClick -> handleLockNowClick()
        is AccountSecurityAction.LoginRequestToggle -> handleLoginRequestToggle(action)
        AccountSecurityAction.LogoutClick -> handleLogoutClick()
        AccountSecurityAction.PendingLoginRequestsClick -> handlePendingLoginRequestsClick()
        is AccountSecurityAction.SessionTimeoutActionSelect -> {
            handleSessionTimeoutActionSelect(action)
        }

        AccountSecurityAction.SessionTimeoutActionClick -> handleSessionTimeoutActionClick()
        AccountSecurityAction.SessionTimeoutClick -> handleSessionTimeoutClick()
        AccountSecurityAction.TwoStepLoginClick -> handleTwoStepLoginClick()
        is AccountSecurityAction.UnlockWithBiometricToggle -> {
            handleUnlockWithBiometricToggled(action)
        }

        is AccountSecurityAction.UnlockWithPinToggle -> handleUnlockWithPinToggle(action)
    }

    private fun handleAccountFingerprintPhraseClick() {
        // TODO BIT-470: Display fingerprint phrase
        sendEvent(AccountSecurityEvent.ShowToast("Display fingerprint phrase.".asText()))
    }

    private fun handleBackClick() = sendEvent(AccountSecurityEvent.NavigateBack)

    private fun handleChangeMasterPasswordClick() {
        // TODO BIT-971: Add Leaving app Dialog
        sendEvent(AccountSecurityEvent.ShowToast("Not yet implemented.".asText()))
    }

    private fun handleConfirmLogoutClick() {
        mutableStateFlow.update { it.copy(dialog = null) }
        authRepository.logout()
    }

    private fun handleDeleteAccountClick() {
        // TODO BIT-1031: Navigate to delete account
        sendEvent(AccountSecurityEvent.ShowToast("Not yet implemented.".asText()))
    }

    private fun handleDismissDialog() {
        mutableStateFlow.update { it.copy(dialog = null) }
    }

    private fun handleLockNowClick() {
        // TODO BIT-467: Lock the app
        sendEvent(AccountSecurityEvent.ShowToast("Lock the app.".asText()))
    }

    private fun handleLoginRequestToggle(action: AccountSecurityAction.LoginRequestToggle) {
        // TODO BIT-466: Persist pending login requests state
        mutableStateFlow.update { it.copy(isApproveLoginRequestsEnabled = action.enabled) }
        sendEvent(AccountSecurityEvent.ShowToast("Handle Login requests on this device.".asText()))
    }

    private fun handleLogoutClick() {
        mutableStateFlow.update { it.copy(dialog = AccountSecurityDialog.ConfirmLogout) }
    }

    private fun handlePendingLoginRequestsClick() {
        // TODO BIT-466: Implement pending login requests UI
        sendEvent(AccountSecurityEvent.ShowToast("Not yet implemented.".asText()))
    }

    private fun handleSessionTimeoutActionSelect(
        action: AccountSecurityAction.SessionTimeoutActionSelect,
    ) {
        // TODO BIT-746: Implement session timeout action
        mutableStateFlow.update {
            it.copy(
                dialog = null,
                sessionTimeoutAction = action.sessionTimeoutAction,
            )
        }
        sendEvent(AccountSecurityEvent.ShowToast("Not yet implemented.".asText()))
    }

    private fun handleSessionTimeoutActionClick() {
        mutableStateFlow.update { it.copy(dialog = AccountSecurityDialog.SessionTimeoutAction) }
    }

    private fun handleSessionTimeoutClick() {
        // TODO BIT-462: Implement session timeout
        sendEvent(AccountSecurityEvent.ShowToast("Display session timeout dialog.".asText()))
    }

    private fun handleTwoStepLoginClick() {
        // TODO BIT-468: Implement two-step login
        sendEvent(AccountSecurityEvent.ShowToast("Not yet implemented.".asText()))
    }

    private fun handleUnlockWithBiometricToggled(
        action: AccountSecurityAction.UnlockWithBiometricToggle,
    ) {
        // TODO Display alert
        mutableStateFlow.update { it.copy(isUnlockWithBiometricsEnabled = action.enabled) }
        sendEvent(AccountSecurityEvent.ShowToast("Handle unlock with biometrics.".asText()))
    }

    private fun handleUnlockWithPinToggle(action: AccountSecurityAction.UnlockWithPinToggle) {
        // TODO BIT-974: Display alert
        mutableStateFlow.update { it.copy(isUnlockWithPinEnabled = action.enabled) }
        sendEvent(AccountSecurityEvent.ShowToast("Handle unlock with pin.".asText()))
    }
}

/**
 * Models state for the Account Security screen.
 */
@Parcelize
data class AccountSecurityState(
    val dialog: AccountSecurityDialog?,
    val isApproveLoginRequestsEnabled: Boolean,
    val isUnlockWithBiometricsEnabled: Boolean,
    val isUnlockWithPinEnabled: Boolean,
    val sessionTimeout: Text,
    val sessionTimeoutAction: SessionTimeoutAction,
) : Parcelable

/**
 * Representation of the dialogs that can be displayed on account security screen.
 */
sealed class AccountSecurityDialog : Parcelable {
    /**
     * Allows the user to confirm that they want to logout.
     */
    @Parcelize
    data object ConfirmLogout : AccountSecurityDialog()

    /**
     * Allows the user to select a session timeout action.
     */
    @Parcelize
    data object SessionTimeoutAction : AccountSecurityDialog()
}

/**
 * A representation of the Session timeout action.
 */
enum class SessionTimeoutAction(val text: Text) {
    LOCK(text = R.string.lock.asText()),
    LOG_OUT(text = R.string.log_out.asText()),
}

/**
 * Models events for the account security screen.
 */
sealed class AccountSecurityEvent {
    /**
     * Navigate back.
     */
    data object NavigateBack : AccountSecurityEvent()

    /**
     * Displays a toast with the given [Text].
     */
    data class ShowToast(
        val text: Text,
    ) : AccountSecurityEvent()
}

/**
 * Models actions for the account security screen.
 */
sealed class AccountSecurityAction {

    /**
     * User clicked account fingerprint phrase.
     */
    data object AccountFingerprintPhraseClick : AccountSecurityAction()

    /**
     * User clicked back button.
     */
    data object BackClick : AccountSecurityAction()

    /**
     * User clicked change master password.
     */
    data object ChangeMasterPasswordClick : AccountSecurityAction()

    /**
     * User confirmed they want to logout.
     */
    data object ConfirmLogoutClick : AccountSecurityAction()

    /**
     * User clicked delete account.
     */
    data object DeleteAccountClick : AccountSecurityAction()

    /**
     * User dismissed the currently displayed dialog.
     */
    data object DismissDialog : AccountSecurityAction()

    /**
     * User clicked lock now.
     */
    data object LockNowClick : AccountSecurityAction()

    /**
     * User toggled the login request switch.
     */
    data class LoginRequestToggle(
        val enabled: Boolean,
    ) : AccountSecurityAction()

    /**
     * User clicked log out.
     */
    data object LogoutClick : AccountSecurityAction()

    /**
     * User clicked pending login requests.
     */
    data object PendingLoginRequestsClick : AccountSecurityAction()

    /**
     * User selected a [SessionTimeoutAction].
     */
    data class SessionTimeoutActionSelect(
        val sessionTimeoutAction: SessionTimeoutAction,
    ) : AccountSecurityAction()

    /**
     * User clicked session timeout action.
     */
    data object SessionTimeoutActionClick : AccountSecurityAction()

    /**
     * User clicked session timeout.
     */
    data object SessionTimeoutClick : AccountSecurityAction()

    /**
     * User clicked two-step login.
     */
    data object TwoStepLoginClick : AccountSecurityAction()

    /**
     * User toggled the unlock with biometrics switch.
     */
    data class UnlockWithBiometricToggle(
        val enabled: Boolean,
    ) : AccountSecurityAction()

    /**
     * User toggled the unlock with pin switch.
     */
    data class UnlockWithPinToggle(
        val enabled: Boolean,
    ) : AccountSecurityAction()
}
