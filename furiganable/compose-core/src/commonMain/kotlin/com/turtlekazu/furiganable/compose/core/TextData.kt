package com.turtlekazu.furiganable.compose.core

/**
 * A data class representing text with optional furigana (phonetic readings).
 *
 * This is used to pass text to components that support displaying both the original text
 * (such as kanji) and its furigana (phonetic readings).
 *
 * @property text The main text to display (e.g., in kanji).
 * @property reading The optional phonetic reading of the text. If `null`, no furigana will be shown.
 */
data class TextData(
    val text: String,
    val reading: String? = null,
)
