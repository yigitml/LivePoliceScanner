package com.oakssoftware.livepolicescanner.ui.screens.about_us

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oakssoftware.livepolicescanner.ui.components.BannerAd
import com.oakssoftware.livepolicescanner.util.Constants
import java.util.Calendar

@Composable
fun AboutUsScreen(
        ip: PaddingValues,
) {
    Scaffold(
            bottomBar = {
                BannerAd(
                        Modifier.fillMaxWidth().padding(ip).navigationBarsPadding(),
                        adUnitId = Constants.BANNER_ABOUT_US
                )
            }
    ) { innerPadding ->
        Surface(Modifier.padding(ip).padding(innerPadding)) {
            Column(
                    Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
            ) {
                Text(
                        "©" +
                                Calendar.getInstance().get(Calendar.YEAR).toString() +
                                " OaksSoftware",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                )
                Spacer(Modifier.padding(16.dp))
                Text(
                        "Designed and Engineered by Ahmet Yiğit DAYI",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                )
            }
        }
    }
}
