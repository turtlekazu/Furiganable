package com.turtlekazu.furiganable.compose.m3

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.turtlekazu.furiganable.compose.core.TextWithReading

/**
 * Material3-compatible variant of [TextWithReading].
 *
 * Resolves the [color] parameter according to Material3 conventions:
 * it uses [color] if explicitly set, otherwise falls back to [style.color],
 * and finally to [LocalContentColor] if neither are specified.
 *
 * Intended for use with [androidx.compose.material3.MaterialTheme].
 * This ensures consistency with Material3's text color and theming behavior.
 *
 * This function supports rendering Japanese text with furigana (phonetic readings) above kanji,
 * using inline markup such as `[漢字[かんじ]]` to denote readings.
 *
 * For detailed control over furigana appearance and layout behavior,
 * see the base implementation in [TextWithReading].
 *
 * @see TextWithReading
 * @see androidx.compose.material3.Text
 * @see androidx.compose.material3.MaterialTheme
 */
@Composable
fun TextWithReadingM3(
    formattedText: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
    showReadings: Boolean = true,
    lineHeightAddRatio: Float = 0.5f,
    furiganaFontSizeRatio: Float = 0.5f,
    furiganaSpacingRatio: Float = 0.1f,
    furiganaLetterSpacingReduceRatio: Float = 0.05f,
) {
    val textColor = color.takeOrElse { style.color.takeOrElse { LocalContentColor.current } }

    TextWithReading(
        formattedText = formattedText,
        style = style,
        color = textColor,
        modifier = modifier,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        showReadings = showReadings,
        lineHeightAddRatio = lineHeightAddRatio,
        furiganaFontSizeRatio = furiganaFontSizeRatio,
        furiganaSpacingRatio = furiganaSpacingRatio,
        furiganaLetterSpacingReduceRatio = furiganaLetterSpacingReduceRatio,
    )
}
