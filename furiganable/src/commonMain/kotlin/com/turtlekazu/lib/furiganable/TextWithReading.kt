package com.turtlekazu.lib.furiganable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

/**
 * The core composable function for displaying text with furigana (reading).
 *
 * @param text the text to be displayed
 * @param color [Color] to apply to the text. If [Color.Unspecified]
 * the text color will be [Color.Black].
 * @param style style configuration for the text such as color, font, line height etc.
 * @param modifier the [Modifier] to be applied to this layout node
 * @param fontSize the size of glyphs to use when painting the text. See [TextStyle.fontSize].
 * @param fontStyle the typeface variant to use when drawing the letters (e.g., italic). See
 *   [TextStyle.fontStyle].
 * @param fontWeight the typeface thickness to use when painting the text (e.g., [FontWeight.Bold]).
 * @param fontFamily the font family to be used when rendering the text. See [TextStyle.fontFamily].
 * @param letterSpacing the amount of space to add between each letter. See
 *   [TextStyle.letterSpacing].
 * @param textDecoration the decorations to paint on the text (e.g., an underline). See
 *   [TextStyle.textDecoration].
 * @param textAlign the alignment of the text within the lines of the paragraph. See
 *   [TextStyle.textAlign].
 * @param lineHeight line height for the [Paragraph] in [TextUnit] unit, e.g. SP or EM. See
 *   [TextStyle.lineHeight].
 * @param overflow how visual overflow should be handled.
 * @param softWrap whether the text should break at soft line breaks. If false, the glyphs in the
 *   text will be positioned as if there was unlimited horizontal space. If [softWrap] is false,
 *   [overflow] and TextAlign may have unexpected effects.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if necessary.
 *   If the text exceeds the given number of lines, it will be truncated according to [overflow] and
 *   [softWrap]. It is required that 1 <= [minLines] <= [maxLines].
 * @param minLines The minimum height in terms of minimum number of visible lines. It is required
 *   that 1 <= [minLines] <= [maxLines].
 * @param onTextLayout callback that is executed when a new text layout is calculated. A
 *  [TextLayoutResult] object that callback provides contains paragraph information, size of the
 *   text, baselines and other details. The callback can be used to add additional decoration or
 *   functionality to the text. For example, to draw selection around the text.
 * @param showReadings If false, the furigana (reading) will not be shown.
 * @param lineHeightAddRatio Extra line height added when furigana (reading) is shown,
 * expressed as a proportion of the main text’s font size.
 * @param furiganaFontSizeRatio Furigana (reading) font size as a proportion of main text font size.
 * @param furiganaSpacingRatio Spacing between furigana (reading) and main text,
 * expressed as a proportion of the main text font size.
 * @param furiganaLetterSpacingReduceRatio Furigana (reading) letter-spacing reduction
 * as a proportion of main text letter spacing.
 * (Ratio means the proportion of the main text's font size.)
 */
@Composable
fun TextWithReading(
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
    showReadings: Boolean = true,
    lineHeightAddRatio: Float = 0.5f,
    furiganaFontSizeRatio: Float = 0.5f,
    furiganaSpacingRatio: Float = 0.1f,
    furiganaLetterSpacingReduceRatio: Float = 0.05f,
) {
    val textColor = color.takeOrElse {
        style.color.takeOrElse { Color.Black }
    }

    if (text.hasReadings() && showReadings) {

        val (textContent, inlineContent) =
            remember(text) {
                calculateAnnotatedString(
                    textDataList = text.toTextData(),
                    showReadings = showReadings,
                    color = textColor,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    letterSpacing = letterSpacing,
                    textDecoration = textDecoration,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    style = style,
                    furiganaFontSizeRatio = furiganaFontSizeRatio,
                    furiganaLetterSpacingReduceRatio = furiganaLetterSpacingReduceRatio,
                    furiganaSpacingRatio = furiganaSpacingRatio,
                )
            }

        val adjustedLineHeight = adjustedLineHeight(
            lineHeight = lineHeight,
            fontSize = fontSize,
            style = style,
            addRatio = lineHeightAddRatio,
        )

        BasicText(
            text = textContent,
            modifier = modifier,
            style = style.merge(
                color = textColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = textAlign ?: TextAlign.Unspecified,
                lineHeight = adjustedLineHeight,
                fontFamily = fontFamily,
                textDecoration = textDecoration,
                fontStyle = fontStyle,
                letterSpacing = letterSpacing
            ),
            onTextLayout = onTextLayout,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = inlineContent,
        )
    } else {
        BasicText(
            text,
            modifier,
            style.merge(
                color = textColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = textAlign ?: TextAlign.Unspecified,
                lineHeight = lineHeight,
                fontFamily = fontFamily,
                textDecoration = textDecoration,
                fontStyle = fontStyle,
                letterSpacing = letterSpacing
            ),
            onTextLayout,
            overflow,
            softWrap,
            maxLines,
            minLines
        )
    }
}

private fun calculateAnnotatedString(
    textDataList: List<TextData>,
    showReadings: Boolean,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    letterSpacing: TextUnit,
    textDecoration: TextDecoration?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    style: TextStyle,
    furiganaFontSizeRatio: Float,
    furiganaLetterSpacingReduceRatio: Float,
    furiganaSpacingRatio: Float,
): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val inlineContent = mutableMapOf<String, InlineTextContent>()

    return buildAnnotatedString {
        for (elem in textDataList) {
            val text = elem.text
            val reading = elem.reading

            if (reading == null) {
                append(text)
                continue
            }

            val mergedStyle = style.merge(
                color = color,
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textAlign = textAlign ?: TextAlign.Unspecified,
                textDecoration = textDecoration,
                lineHeight = lineHeight,
            )
            val height = mergedStyle.lineHeight
            val width = (text.length.toDouble() + (text.length - 1) * 0.05).em

            appendInlineContent(text, text)
            inlineContent[text] = InlineTextContent(
                placeholder =
                    Placeholder(
                        width = width,
                        height = height,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                    ),
                children = {
                    val readingFontSize = mergedStyle.fontSize * furiganaFontSizeRatio

                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        TextSpacingRemoved(
                            modifier = Modifier.wrapContentWidth(unbounded = true),
                            text = text,
                            color = mergedStyle.color,
                            style = mergedStyle,
                        )

                        if (showReadings) {
                            val adjustedfuriganaSpacingRatio =
                                1 + furiganaSpacingRatio + getFuriganaSpacingCompensation()
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    translationY = -0.5f * readingFontSize.toPx() +
                                            -0.5f * mergedStyle.fontSize.toPx() *
                                            adjustedfuriganaSpacingRatio
                                }
                            ) {
                                val adjustedLetterSpacing = if (fontSize.isSpecified) {
                                    (-fontSize.value * furiganaLetterSpacingReduceRatio).sp
                                } else {
                                    (-style.fontSize.value * furiganaLetterSpacingReduceRatio).sp
                                }

                                BasicText(
                                    modifier = Modifier.wrapContentWidth(unbounded = true),
                                    text = reading,
                                    style = style.copy(
                                        fontSize = readingFontSize,
                                        letterSpacing = adjustedLetterSpacing,
                                    ),
                                )
                            }
                        }
                    }
                },
            )
        }
    } to inlineContent
}

private fun adjustedLineHeight(
    lineHeight: TextUnit,
    fontSize: TextUnit,
    style: TextStyle,
    addRatio: Float = 0.6f
): TextUnit = when {
    lineHeight.isSpecified && fontSize.isSpecified ->
        (lineHeight.value + style.fontSize.value * addRatio).sp

    lineHeight.isSpecified ->
        (lineHeight.value + style.fontSize.value * addRatio).sp

    fontSize.isSpecified ->
        (style.lineHeight.value + fontSize.value * addRatio).sp

    else ->
        (style.lineHeight.value + style.fontSize.value * addRatio).sp
}