package com.oakssoftware.livepolicescanner.ui.screens.stations.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCloseClicked) {
            Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_chevron_left_24),
                    contentDescription = "Back"
            )
        }
        TextField(
                modifier = Modifier.weight(1f),
                value = searchText,
                onValueChange = onSearch,
                placeholder = { Text(text = hint) },
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { onSearch("") }) {
                            Icon(
                                    imageVector =
                                            ImageVector.vectorResource(R.drawable.outline_close_24),
                                    contentDescription = "Clear"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch(searchText) }),
                colors =
                        TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                        )
        )
    }
}
