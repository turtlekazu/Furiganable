package com.turtlekazu.furiganable.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.turtlekazu.lib.furiganable.TextWithReadingM2
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TextWithReadingM2(
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
                style = MaterialTheme.typography.h3,
            )

            TextWithReadingM2(
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
                style = MaterialTheme.typography.h4,
            )

            TextWithReadingM2(
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
                style = MaterialTheme.typography.h5,
            )

            TextWithReadingM2(
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
                style = MaterialTheme.typography.h6,
            )

            TextWithReadingM2(
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
                style = MaterialTheme.typography.body1,
            )

            TextWithReadingM2(
                "これは[試験用[しけんよう]]の[文字列[もじれつ]]です。",
                style = MaterialTheme.typography.subtitle1,
            )
        }
    }
}
