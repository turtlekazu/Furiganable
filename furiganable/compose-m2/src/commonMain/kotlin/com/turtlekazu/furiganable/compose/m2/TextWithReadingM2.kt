package com.turtlekazu.furiganable.compose.m2

import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
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
 * Material2-compatible variant of [TextWithReading].
 *
 * Resolves the [color] parameter according to Material2 conventions:
 * it uses [color] if explicitly set, otherwise falls back to [style.color],
 * and finally to [LocalContentColor] with [LocalContentAlpha] if neither are specified.
 *
 * Intended for use with [androidx.compose.material.MaterialTheme].
 * This ensures consistency with Material2's text color and content alpha behavior.
 *
 * This function supports rendering Japanese text with furigana (phonetic readings) above kanji,
 * using inline markup such as `[漢字[かんじ]]` to denote readings.
 *
 * For detailed control over furigana appearance and layout behavior,
 * see the base implementation in [TextWithReading].
 *
 * @see TextWithReading
 * @see androidx.compose.material.Text
 * @see androidx.compose.material.MaterialTheme
 */
@Composable
fun TextWithReadingM2(
    text: String,
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
    val localContentColor = LocalContentColor.current
    val localContentAlpha = LocalContentAlpha.current
    val overrideColorOrUnspecified: Color =
        if (color.isSpecified) {
            color
        } else if (style.color.isSpecified) {
            style.color
        } else {
            localContentColor.copy(localContentAlpha)
        }

    TextWithReading(
        text = text,
        style = style,
        color = overrideColorOrUnspecified,
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
