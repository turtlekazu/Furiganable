package com.turtlekazu.furiganable.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turtlekazu.lib.furiganable.TextWithReadingM2

@Composable
fun App() {
    MaterialTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            val text =
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。[複数行[ふくすうぎょう]]にも[対応[たいおう]]していることを[検証[けんしょう]]するために[長[なが]]くしています。"
            val textAlt =
                "This is a test string. It is long to verify that it supports multiple lines."
            Column(
                Modifier.fillMaxWidth().safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TextWithReadingM2(
                    text,
                    style = MaterialTheme.typography.h3,
                )

                TextWithReadingM2(
                    textAlt,
                    style = MaterialTheme.typography.h3,
                )

                TextWithReadingM2(
                    text,
                    style = MaterialTheme.typography.h4,
                )

                TextWithReadingM2(
                    textAlt,
                    style = MaterialTheme.typography.h4,
                )

                TextWithReadingM2(
                    text,
                    style = MaterialTheme.typography.h5,
                )

                TextWithReadingM2(
                    textAlt,
                    style = MaterialTheme.typography.h5,
                )

                TextWithReadingM2(
                    text,
                    style = MaterialTheme.typography.h6,
                )

                TextWithReadingM2(
                    textAlt,
                    style = MaterialTheme.typography.h6,
                )

                TextWithReadingM2(
                    text,
                    style = MaterialTheme.typography.body1,
                )

                TextWithReadingM2(
                    textAlt,
                    style = MaterialTheme.typography.body1,
                )

                TextWithReadingM2(
                    text,
                    style = MaterialTheme.typography.subtitle1,
                )

                TextWithReadingM2(
                    textAlt,
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        }
    }
}
