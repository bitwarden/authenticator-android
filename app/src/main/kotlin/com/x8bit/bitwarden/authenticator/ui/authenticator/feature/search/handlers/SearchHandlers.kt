package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.search.handlers

import com.x8bit.bitwarden.authenticator.ui.authenticator.feature.search.ItemSearchAction
import com.x8bit.bitwarden.authenticator.ui.authenticator.feature.search.ItemSearchViewModel

class SearchHandlers(
    val onBackClick: () -> Unit,
    val onDismissRequest: () -> Unit,
    val onItemClick: (String) -> Unit,
    val onSearchTermChange: (String) -> Unit,
) {
    companion object {
        fun create(viewModel: ItemSearchViewModel): SearchHandlers =
            SearchHandlers(
                onBackClick = { viewModel.trySendAction(ItemSearchAction.BackClick) },
                onDismissRequest = { viewModel.trySendAction(ItemSearchAction.DismissDialogClick) },
                onItemClick = { viewModel.trySendAction(ItemSearchAction.ItemClick(it)) },
                onSearchTermChange = {
                    viewModel.trySendAction(ItemSearchAction.SearchTermChange(it))
                }
            )
    }
}
