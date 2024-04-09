package com.x8bit.bitwarden.authenticator.ui.authenticator.feature.search

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x8bit.bitwarden.authenticator.ui.authenticator.feature.search.handlers.SearchHandlers
import com.x8bit.bitwarden.authenticator.ui.platform.components.listitem.BitwardenListItem
import com.x8bit.bitwarden.authenticator.ui.platform.components.listitem.SelectionItemData
import kotlinx.collections.immutable.toPersistentList

@Composable
fun ItemSearchContent(
    viewState: ItemSearchState.ViewState.Content,
    searchHandlers: SearchHandlers,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(viewState.displayItems) {
            BitwardenListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        // There is some built-in padding to the menu button that makes up
                        // the visual difference here.
                        end = 12.dp,
                    ),
                label = it.accountName,
                startIcon = it.startIcon,
                onClick = { searchHandlers.onItemClick(it.id) },
                selectionDataList = emptyList<SelectionItemData>().toPersistentList()
            )
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
