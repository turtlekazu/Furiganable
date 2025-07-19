package com.turtlekazu.furiganable.compose.core

/**
 * Splits the receiver into segments of normal text and **furigana** pairs.
 *
 * The parser looks for the marker **`[漢字[かんじ]]`** :
 *
 * * The first bracketed chunk (`漢字`) becomes **[TextData.text]**.
 * * The nested chunk (`かんじ`) becomes **[TextData.reading]**.
 * * All text outside the marker is returned unchanged with *reading = null*.
 *
 * The original order is preserved.
 * If the string contains no markers, the whole string is returned as a single
 * `TextData` with *reading = null*.
 *
 * @receiver A string that may embed furigana markers.
 * @return Ordered `List<TextData>` ready for rendering.
 *
 * ### Example
 * ```kotlin
 * val parts = "これは[漢字[かんじ]]です".toTextData()
 * // [
 * //   TextData(text = "これは"),
 * //   TextData(text = "漢字", reading = "かんじ"),
 * //   TextData(text = "です")
 * // ]
 * ```
 */
fun String.toTextData(): List<TextData> {
    val regex = "\\[([^\\[]+?)\\[([^]]+?)]]".toRegex()
    val result = mutableListOf<TextData>()
    var lastIndex = 0

    regex.findAll(this).forEach { match ->
        if (match.range.first > lastIndex) {
            result.add(TextData(this.substring(lastIndex, match.range.first)))
        }

        result.add(TextData(match.groupValues[1], match.groupValues[2]))

        lastIndex = match.range.last + 1
    }

    if (lastIndex < this.length) {
        result.add(TextData(this.substring(lastIndex)))
    }

    return result
}

internal fun String.hasReadings(): Boolean = this.contains("[") && this.contains("]]")
