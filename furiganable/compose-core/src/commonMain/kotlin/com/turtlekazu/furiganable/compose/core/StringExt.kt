package com.turtlekazu.furiganable.compose.core

internal fun String.hasReadings(): Boolean {
    return this.contains("[") && this.contains("]]")
}

internal fun String.toTextData(): List<TextData> {
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
