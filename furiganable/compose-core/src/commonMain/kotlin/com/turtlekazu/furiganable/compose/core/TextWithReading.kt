package com.turtlekazu.furiganable.compose.core

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
 * The core composable for displaying text with optional furigana (phonetic readings).
 *
 * This function renders the given [text] along with its readings (if any and if [showReadings] is true),
 * positioning the furigana above the corresponding characters. This is especially useful for displaying
 * Japanese kanji with phonetic guides.
 *
 * All standard text styling parameters behave similarly to [androidx.compose.material3.Text].
 * If you are setting your own [style], consider using [LocalTextStyle] and [TextStyle.copy] to retain
 * theme-based defaults.
 *
 * @param text The text to be displayed. May include furigana data formatted like `[漢字[かんじ]]`.
 * @param showReadings Whether to display the furigana. If false, only the base text is shown.
 * @param lineHeightAddRatio Additional line height added when furigana is shown,
 * expressed as a ratio of the main text’s font size (e.g., 0.5 = +50%).
 * @param furiganaFontSizeRatio Font size of the furigana, as a ratio of the main text font size.
 * @param furiganaSpacingRatio Vertical spacing between furigana and base text, as a ratio of font size.
 * @param furiganaLetterSpacingReduceRatio Letter spacing reduction for furigana, as a ratio of the base letter spacing.
 *
 * @param modifier Modifier to apply to the layout.
 * @param color Text color. If [Color.Unspecified], falls back to [style.color] or [LocalContentColor].
 * @param style Text style configuration such as font, color, line height, etc.
 * @param fontSize Font size for the main text.
 * @param fontStyle Typeface variant to use (e.g., italic).
 * @param fontWeight Font thickness to use (e.g., [FontWeight.Bold]).
 * @param fontFamily Font family to use.
 * @param letterSpacing Space to add between letters.
 * @param textDecoration Decorations to apply (e.g., underline).
 * @param textAlign Alignment of text within the paragraph.
 * @param lineHeight Line height in [TextUnit] (e.g., sp or em).
 * @param overflow How to handle visual overflow.
 * @param softWrap Whether to wrap at soft line breaks.
 * @param maxLines Maximum number of lines to display. Required: 1 <= [minLines] <= [maxLines].
 * @param minLines Minimum number of visible lines. Required: 1 <= [minLines] <= [maxLines].
 * @param onTextLayout Callback triggered when a new text layout is calculated.
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
    val textColor =
        color.takeOrElse {
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

        val adjustedLineHeight =
            adjustedLineHeight(
                lineHeight = lineHeight,
                fontSize = fontSize,
                style = style,
                addRatio = lineHeightAddRatio,
            )

        BasicText(
            text = textContent,
            modifier = modifier,
            style =
                style.merge(
                    color = textColor,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textAlign = textAlign ?: TextAlign.Unspecified,
                    lineHeight = adjustedLineHeight,
                    fontFamily = fontFamily,
                    textDecoration = textDecoration,
                    fontStyle = fontStyle,
                    letterSpacing = letterSpacing,
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
                letterSpacing = letterSpacing,
            ),
            onTextLayout,
            overflow,
            softWrap,
            maxLines,
            minLines,
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

            val mergedStyle =
                style.merge(
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
            inlineContent[text] =
                InlineTextContent(
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
                                    modifier =
                                        Modifier.graphicsLayer {
                                            translationY = -0.5f * readingFontSize.toPx() +
                                                -0.5f * mergedStyle.fontSize.toPx() *
                                                adjustedfuriganaSpacingRatio
                                        },
                                ) {
                                    val adjustedLetterSpacing =
                                        if (fontSize.isSpecified) {
                                            (-fontSize.value * furiganaLetterSpacingReduceRatio).sp
                                        } else {
                                            (-style.fontSize.value * furiganaLetterSpacingReduceRatio).sp
                                        }

                                    BasicText(
                                        modifier = Modifier.wrapContentWidth(unbounded = true),
                                        text = reading,
                                        style =
                                            style.copy(
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
    addRatio: Float = 0.6f,
): TextUnit =
    when {
        lineHeight.isSpecified && fontSize.isSpecified ->
            (lineHeight.value + style.fontSize.value * addRatio).sp

        lineHeight.isSpecified ->
            (lineHeight.value + style.fontSize.value * addRatio).sp

        fontSize.isSpecified ->
            (style.lineHeight.value + fontSize.value * addRatio).sp

        else ->
            (style.lineHeight.value + style.fontSize.value * addRatio).sp
    }
