package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.navbar

import com.x8bit.bitwarden.authenticator.data.auth.repository.AuthRepository
import com.x8bit.bitwarden.authenticator.ui.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticatorNavBarViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) :
    BaseViewModel<Unit, AuthenticatorNavBarEvent, AuthenticatorNavBarAction>(
        initialState = Unit,
    ) {

    override fun handleAction(action: AuthenticatorNavBarAction) {
        when (action) {
            AuthenticatorNavBarAction.SettingsTabClick -> {
                handleSettingsClick()
            }

            AuthenticatorNavBarAction.VerificationCodesTabClick -> {
                handleVerificationCodesTabClick()
            }

            AuthenticatorNavBarAction.BackStackUpdate -> {
                authRepository.updateLastActiveTime()
            }
        }
    }

    private fun handleSettingsClick() {
        sendEvent(AuthenticatorNavBarEvent.NavigateToSettings)
    }

    private fun handleVerificationCodesTabClick() {
        sendEvent(AuthenticatorNavBarEvent.NavigateToVerificationCodes)
    }
}

sealed class AuthenticatorNavBarEvent {
    data object NavigateToVerificationCodes : AuthenticatorNavBarEvent()

    data object NavigateToSettings : AuthenticatorNavBarEvent()
}

sealed class AuthenticatorNavBarAction {
    data object VerificationCodesTabClick : AuthenticatorNavBarAction()

    data object SettingsTabClick : AuthenticatorNavBarAction()

    data object BackStackUpdate : AuthenticatorNavBarAction()
}
