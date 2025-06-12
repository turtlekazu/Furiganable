package com.turtlekazu.furiganable.compose.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
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
 * This function renders the given [formattedText] along with its readings (if any and if [furiganaEnabled] is true),
 * positioning the furigana above the corresponding characters. This is especially useful for displaying
 * Japanese kanji with phonetic guides.
 *
 * All standard text styling parameters behave similarly to [androidx.compose.material3.Text].
 * If you are setting your own [style], consider using [LocalTextStyle] and [TextStyle.copy] to retain
 * theme-based defaults.
 *
 * @param formattedText The text to be displayed. May include furigana data formatted like `[漢字[かんじ]]`.
 * @param furiganaEnabled Whether to enable the furigana. If false, normal text component will be used.
 * @param furiganaFontSize Font size for the furigana text. If unspecified, main text fontSize * 0.5f.
 * @param furiganaLineHeight Line height for the furigana text. If unspecified, uses `furiganaFontSize * 1.2f`.
 * @param furiganaLetterSpacing Letter spacing for the furigana text. If unspecified, uses `-style.fontSize * 0.03f`.
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
    formattedText: String,
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
    furiganaEnabled: Boolean = true,
    furiganaFontSize: TextUnit = TextUnit.Unspecified,
    furiganaLineHeight: TextUnit = TextUnit.Unspecified,
    furiganaLetterSpacing: TextUnit = TextUnit.Unspecified,
) {
    val textColor =
        color.takeOrElse {
            style.color.takeOrElse { DEFAULT_COLOR }
        }

    val mergedStyle =
        style.merge(
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textAlign = textAlign ?: TextAlign.Unspecified,
            textDecoration = textDecoration,
            lineHeight = lineHeight,
        )

    if (formattedText.hasReadings() && furiganaEnabled) {
        val (textContent, inlineContent) =
            remember(formattedText) {
                calculateAnnotatedString(
                    textDataList = formattedText.toTextData(),
                    showReadings = furiganaEnabled,
                    style = mergedStyle,
                    furiganaFontSize = furiganaFontSize,
                    furiganaLetterSpacing = furiganaLetterSpacing,
                )
            }

        val totalLineHeight = calculateLineHeight(
            lineHeight = lineHeight,
            style = mergedStyle,
            furiganaLineHeight = furiganaLineHeight,
            furiganaFontSize = furiganaFontSize,
        )

        BasicText(
            text = textContent,
            modifier = modifier,
            style =
                mergedStyle.merge(
                    lineHeight = totalLineHeight,
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
            formattedText,
            modifier,
            mergedStyle,
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
    style: TextStyle,
    furiganaFontSize: TextUnit,
    furiganaLetterSpacing: TextUnit,
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
            val height = if (style.lineHeight.isSpecified) {
                style.lineHeight
            } else if (style.fontSize.isSpecified) {
                style.fontSize * 1.2f
            } else {
                DEFAULT_FONT_SIZE.sp
            }

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
                        val readingFontSize = when {
                            furiganaFontSize.isSpecified -> furiganaFontSize
                            style.fontSize.isSpecified -> style.fontSize * 0.5f
                            else -> DEFAULT_FONT_SIZE.sp * 0.5f
                        }

                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            TextSpacingRemoved(
                                modifier = Modifier.wrapContentWidth(unbounded = true),
                                text = text,
                                color = style.color,
                                style = style,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Visible,
                            )

                            val realLineHeight = when {
                                style.lineHeight.isSpecified -> style.lineHeight
                                style.fontSize.isSpecified -> style.fontSize * 1.2f
                                else -> DEFAULT_FONT_SIZE.sp * 1.2f
                            }

                            val realFontSize = when {
                                style.fontSize.isSpecified -> style.fontSize
                                else -> DEFAULT_FONT_SIZE.sp
                            }

                            if (showReadings) {
                                Box(
                                    modifier = Modifier
                                        .graphicsLayer {
                                            translationY = -(
                                                realLineHeight.toPx() * 0.5f +
                                                    readingFontSize.toPx() * 0.5f +
                                                    realFontSize.toPx() * getFuriganaSpacingCompensation()
                                                )
                                        },
                                ) {
                                    val adjustedLetterSpacing = when {
                                        furiganaLetterSpacing.isSpecified -> furiganaLetterSpacing
                                        style.fontSize.isSpecified -> -style.fontSize * 0.03f
                                        else -> -(DEFAULT_FONT_SIZE.sp) * 0.03f
                                    }
                                    BasicText(
                                        modifier = Modifier.wrapContentSize(),
                                        text = reading,
                                        softWrap = false,
                                        maxLines = 1,
                                        overflow = TextOverflow.Visible,
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

private fun calculateLineHeight(
    lineHeight: TextUnit,
    style: TextStyle,
    furiganaFontSize: TextUnit,
    furiganaLineHeight: TextUnit,
): TextUnit {
    val baseLineHeight = when {
        lineHeight.isSpecified -> lineHeight
        style.lineHeight.isSpecified -> style.lineHeight
        style.fontSize.isSpecified -> style.fontSize * 1.2f
        else -> DEFAULT_FONT_SIZE.sp * 1.2f
    }

    val furiganaHeight = when {
        furiganaLineHeight.isSpecified -> furiganaLineHeight
        furiganaFontSize.isSpecified -> furiganaFontSize * 1.2f
        style.fontSize.isSpecified -> style.fontSize * 0.5f * 1.2f
        else -> DEFAULT_FONT_SIZE.sp * 0.5f * 1.2f
    }

    return (baseLineHeight.value + furiganaHeight.value).sp
}
