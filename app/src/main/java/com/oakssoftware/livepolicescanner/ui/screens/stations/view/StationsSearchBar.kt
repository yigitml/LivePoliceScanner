package com.oakssoftware.livepolicescanner.ui.screens.stations.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.oakssoftware.livepolicescanner.R

@Composable
fun StationsSearchBar(
    hint: String = "",
    searchText: String,
    onSearch: (String) -> Unit = {},
    onCloseClicked: () -> Unit = {}
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = searchText,
        onValueChange = onSearch,
        placeholder = { Text(text = hint) },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        leadingIcon = {
            IconButton(onClick = onCloseClicked) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.outline_close_24), contentDescription = "Close Icon")
            }
        },
        trailingIcon = {
            IconButton(onClick = { onSearch(searchText) }) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.outline_search_24), contentDescription = "Search Icon")
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(searchText) })
    )
}