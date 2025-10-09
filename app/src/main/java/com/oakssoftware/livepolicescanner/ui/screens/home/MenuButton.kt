package com.oakssoftware.livepolicescanner.ui.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oakssoftware.livepolicescanner.ui.theme.PoliceScannerProTheme

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 16.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewMenuButton() {
    PoliceScannerProTheme {
        MenuButton(onClick = { }, text = "Police Scanner")
    }
}