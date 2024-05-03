package com.bitwarden.authenticator.ui.platform.feature.settings.importing

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.bitwarden.authenticator.R
import com.bitwarden.authenticator.data.authenticator.repository.AuthenticatorRepository
import com.bitwarden.authenticator.data.authenticator.repository.model.ImportDataResult
import com.bitwarden.authenticator.ui.platform.base.BaseViewModel
import com.bitwarden.authenticator.ui.platform.base.util.Text
import com.bitwarden.authenticator.ui.platform.base.util.asText
import com.bitwarden.authenticator.ui.platform.feature.settings.importing.model.ImportFormat
import com.bitwarden.authenticator.ui.platform.manager.intent.IntentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import javax.inject.Inject

/**
 * View model for the Importing screen.
 */
@HiltViewModel
class ImportingViewModel @Inject constructor(
    private val authenticatorRepository: AuthenticatorRepository,
) :
    BaseViewModel<ImportState, ImportEvent, ImportAction>(
        initialState = ImportState(importFormat = ImportFormat.JSON)
    ) {

    override fun handleAction(action: ImportAction) {
        when (action) {
            ImportAction.CloseButtonClick -> {
                handleCloseButtonClick()
            }

            ImportAction.ImportClick -> {
                handleConfirmImportClick()
            }

            ImportAction.DialogDismiss -> {
                handleDialogDismiss()
            }

            is ImportAction.ImportFormatOptionSelect -> {
                handleImportFormatOptionSelect(action)
            }

            is ImportAction.ImportLocationReceive -> {
                handleImportLocationReceive(action)
            }

            is ImportAction.Internal -> {
                handleInternalAction(action)
            }
        }
    }

    private fun handleCloseButtonClick() {
        sendEvent(ImportEvent.NavigateBack)
    }

    private fun handleConfirmImportClick() {
        sendEvent(ImportEvent.NavigateToSelectImportFile(state.importFormat))
    }

    private fun handleDialogDismiss() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }

    private fun handleImportFormatOptionSelect(action: ImportAction.ImportFormatOptionSelect) {
        mutableStateFlow.update { it.copy(importFormat = action.option) }
    }

    private fun handleImportLocationReceive(action: ImportAction.ImportLocationReceive) {
        mutableStateFlow.update { it.copy(dialogState = ImportState.DialogState.Loading()) }
        viewModelScope.launch {
            val result = authenticatorRepository.importVaultData(state.importFormat, action.fileUri)
            sendAction(ImportAction.Internal.SaveImportDataToUriResultReceive(result))
        }
    }

    private fun handleInternalAction(action: ImportAction.Internal) {
        when (action) {
            is ImportAction.Internal.SaveImportDataToUriResultReceive -> {
                handleSaveImportDataToUriResultReceive(action.result)
            }
        }
    }

    private fun handleSaveImportDataToUriResultReceive(result: ImportDataResult) {
        when (result) {
            ImportDataResult.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ImportState.DialogState.Error(
                            title = R.string.an_error_has_occurred.asText(),
                            message = R.string.import_vault_failure.asText(),
                        )
                    )
                }
            }

            ImportDataResult.Success -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
                sendEvent(
                    ImportEvent.ShowToast(
                        message = R.string.import_success.asText(),
                    )
                )
                sendEvent(ImportEvent.NavigateBack)
            }
        }
    }
}

/**
 * Represents state for the [ImportingScreen].
 */
data class ImportState(
    @IgnoredOnParcel
    val fileUri: Uri? = null,
    val dialogState: DialogState? = null,
    val importFormat: ImportFormat,
) {

    /**
     * Represents the current state of any dialogs on the screen.
     */
    sealed class DialogState {

        /**
         * Represents a loading dialog with the given [message].
         */
        data class Loading(
            val message: Text = R.string.loading.asText(),
        ) : DialogState()

        /**
         * Represents a dismissible dialog with the given error [title] and [message].
         */
        data class Error(
            val title: Text? = null,
            val message: Text,
        ) : DialogState()
    }
}

/**
 * Models events for the [ImportingScreen].
 */
sealed class ImportEvent {

    /**
     * Navigate back to the previous screen.
     */
    data object NavigateBack : ImportEvent()

    /**
     * Show a Toast with the given [message].
     */
    data class ShowToast(val message: Text) : ImportEvent()

    /**
     * Navigate to the select import file screen.
     */
    data class NavigateToSelectImportFile(val importFormat: ImportFormat) : ImportEvent()
}

sealed class ImportAction {
    data object CloseButtonClick : ImportAction()

    data object ImportClick : ImportAction()

    data object DialogDismiss : ImportAction()

    data class ImportFormatOptionSelect(val option: ImportFormat) : ImportAction()

    data class ImportLocationReceive(val fileUri: IntentManager.FileData) : ImportAction()

    sealed class Internal : ImportAction() {

        data class SaveImportDataToUriResultReceive(
            val result: ImportDataResult,
        ) : Internal()
    }
}
