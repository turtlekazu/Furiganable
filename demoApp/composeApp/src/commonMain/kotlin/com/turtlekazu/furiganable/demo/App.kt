package com.turtlekazu.furiganable.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turtlekazu.furiganable.compose.m2.TextWithReading
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            val text =
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。[複数行[ふくすうぎょう]]" +
                    "にも[対応[たいおう]]していることを[検証[けんしょう]]するために[長[なが]]くしています。"
            val textAlt =
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。"
            val stylesList =
                listOf(
                    MaterialTheme.typography.h4,
                    MaterialTheme.typography.h5,
                    MaterialTheme.typography.h6,
                    MaterialTheme.typography.body1,
                    MaterialTheme.typography.subtitle1,
                )

            Column(
                Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TextWithReading(
                    formattedText = "Compose Material 2",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(vertical = 30.dp),
                )

                TextWithReading(
                    textAlt,
                    style = MaterialTheme.typography.h3,
                )

                stylesList.forEach { style ->
                    TextWithReading(
                        formattedText = text,
                        style = style,
                    )
                }

                com.turtlekazu.furiganable.compose.m3.TextWithReading(
                    formattedText = "Compose Material 3",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(vertical = 30.dp),
                )

                com.turtlekazu.furiganable.compose.m3.TextWithReading(
                    textAlt,
                    style = MaterialTheme.typography.h3,
                )

                stylesList.forEach { style ->
                    com.turtlekazu.furiganable.compose.m3.TextWithReading(
                        formattedText = text,
                        style = style,
                    )
                }
            }
        }
    }
}
