package com.turtlekazu.furiganable.compose.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * A platform-specific text composable that eliminates unwanted vertical padding caused by default text rendering behavior.
 *
 * On **iOS**, this composable directly delegates to [BasicText].
 *
 * On **Android**, behavior varies by SDK version:
 * - On API level **P (28) and above**, a custom implementation wraps a native `TextView` to explicitly disable
 *   [TextView.isFallbackLineSpacing] and [TextView.setIncludeFontPadding], which cannot be controlled
 *   through Compose's [BasicText].
 * - On API levels **below P**, [BasicText] is used directly since the padding issue does not apply.
 *
 * This composable addresses layout inconsistencies caused by undesired extra spacing above or below text,
 * particularly in multi-line text scenarios, due to platform-specific font metric handling.
 *
 * @param text The text to display.
 * @param color The color to apply to the text.
 * @param style The base [TextStyle] to apply.
 * @param modifier The [Modifier] to be applied to the layout.
 * @param fontSize The font size to use.
 * @param fontStyle The font style to use (e.g., italic).
 * @param fontWeight The font weight to use (e.g., [FontWeight.Bold]).
 * @param fontFamily The font family to use.
 * @param letterSpacing The amount of space between characters.
 * @param textDecoration Decorations to apply (e.g., underline).
 * @param textAlign The alignment of the text within its container.
 * @param lineHeight The height of each line of text.
 * @param overflow How visual overflow should be handled.
 * @param softWrap Whether the text should wrap at soft line breaks.
 * @param maxLines The maximum number of lines to display.
 * @param minLines The minimum number of lines to display.
 * @param onTextLayout Callback triggered when text layout is completed.
 */
@Composable
internal expect fun TextSpacingRemoved(
    text: String,
    color: Color,
    style: TextStyle,
    modifier: Modifier = Modifier,
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
)
