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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import kotlin.math.max

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
 * @param furiganaGap Space between the main text and the furigana. If unspecified, uses `style.fontSize * 0.03f`.
 * @param furiganaFontSize Font size for the furigana text. If unspecified, `style.fontSize * 0.45f`.
 * @param furiganaLineHeight Line height for the furigana text. If unspecified, uses `furiganaFontSize`.
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
fun TextWithReadingCore(
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
    furiganaGap: TextUnit = TextUnit.Unspecified,
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
        val resolvedFontSize =
            when {
                mergedStyle.fontSize.isSpecified -> mergedStyle.fontSize
                else -> DEFAULT_FONT_SIZE.sp
            }

        val resolvedLetterSpacing =
            when {
                mergedStyle.letterSpacing.isSpecified -> mergedStyle.letterSpacing
                else -> DEFAULT_LETTER_SPACING.sp
            }

        val resolvedFuriganaGap =
            when {
                furiganaGap.isSpecified -> furiganaGap
                else -> resolvedFontSize * 0.03f
            }

        val resolvedFuriganaLetterSpacing =
            if (furiganaLetterSpacing.isSpecified) {
                furiganaLetterSpacing
            } else {
                -resolvedFontSize * 0.03f
            }

        val resolvedFuriganaFontSize =
            if (furiganaFontSize.isSpecified) furiganaFontSize else resolvedFontSize * 0.45f

        val resolvedFuriganaLineHeight =
            if (furiganaLineHeight.isSpecified) {
                furiganaLineHeight
            } else {
                resolvedFuriganaFontSize
            }

        val minLineHeight =
            (
                resolvedFontSize.value +
                    max(resolvedFuriganaFontSize.value, resolvedFuriganaLineHeight.value) +
                    resolvedFuriganaGap.value
            ).sp

        val resolvedLineHeight =
            when {
                mergedStyle.lineHeight.isSpecified -> {
                    if (mergedStyle.lineHeight > minLineHeight) {
                        mergedStyle.lineHeight
                    } else {
                        minLineHeight
                    }
                }

                else -> minLineHeight
            }

        val density = LocalDensity.current
        val fontResolver = LocalFontFamilyResolver.current

        val (textContent, inlineContent) =
            remember(formattedText) {
                calculateAnnotatedString(
                    textDataList = formattedText.toTextData(),
                    showReadings = furiganaEnabled,
                    style =
                        mergedStyle.merge(
                            fontSize = resolvedFontSize,
                            letterSpacing = resolvedLetterSpacing,
                        ),
                    furiganaGap = resolvedFuriganaGap,
                    furiganaFontSize = resolvedFuriganaFontSize,
                    furiganaLineHeight = resolvedFuriganaLineHeight,
                    furiganaLetterSpacing = resolvedFuriganaLetterSpacing,
                    fontResolver = fontResolver,
                    density = density,
                )
            }

        BasicText(
            text = textContent,
            modifier = modifier,
            style =
                mergedStyle.merge(
                    lineHeight = resolvedLineHeight,
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
    furiganaGap: TextUnit,
    furiganaFontSize: TextUnit,
    furiganaLineHeight: TextUnit,
    furiganaLetterSpacing: TextUnit,
    fontResolver: FontFamily.Resolver,
    density: Density,
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
            val height =
                if (style.lineHeight.isSpecified) {
                    style.lineHeight
                } else if (style.fontSize.isSpecified) {
                    style.fontSize
                } else {
                    DEFAULT_FONT_SIZE.sp
                }

            val furiganaWidth =
                measureWidthPx(
                    text = reading,
                    style =
                        style.merge(
                            fontSize = furiganaFontSize,
                            letterSpacing = furiganaLetterSpacing,
                        ),
                    density = density,
                    layoutDirection = LayoutDirection.Ltr,
                    fontFamilyResolver = fontResolver,
                )
            val textWidth =
                measureWidthPx(
                    text = text,
                    style = style,
                    density = density,
                    layoutDirection = LayoutDirection.Ltr,
                    fontFamilyResolver = fontResolver,
                )

            val width = max(furiganaWidth, textWidth).sp

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
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            TextSpacingRemoved(
                                modifier = Modifier.wrapContentWidth(unbounded = true),
                                text = text,
                                color = style.color,
                                style =
                                    style.merge(
                                        lineHeightStyle =
                                            LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Proportional,
                                                trim = LineHeightStyle.Trim.Both,
                                            ),
                                    ),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Visible,
                            )

                            if (showReadings) {
                                Box(
                                    modifier =
                                        Modifier
                                            .graphicsLayer {
                                                translationY =
                                                    -(
                                                        style.fontSize.toPx() * 0.5f +
                                                            furiganaFontSize.toPx() * 0.5f +
                                                            furiganaGap.toPx()
                                                    )
                                            },
                                ) {
                                    TextSpacingRemoved(
                                        modifier = Modifier.wrapContentSize(unbounded = true),
                                        text = reading,
                                        softWrap = false,
                                        maxLines = 1,
                                        overflow = TextOverflow.Visible,
                                        color = style.color,
                                        style =
                                            style.merge(
                                                fontSize = furiganaFontSize,
                                                lineHeight = furiganaLineHeight,
                                                lineHeightStyle =
                                                    LineHeightStyle(
                                                        alignment = LineHeightStyle.Alignment.Proportional,
                                                        trim = LineHeightStyle.Trim.Both,
                                                    ),
                                                letterSpacing = furiganaLetterSpacing,
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

private fun measureWidthPx(
    text: String,
    style: TextStyle,
    density: Density,
    layoutDirection: LayoutDirection,
    fontFamilyResolver: FontFamily.Resolver,
): Float {
    val measurer =
        TextMeasurer(
            defaultLayoutDirection = layoutDirection,
            defaultFontFamilyResolver = fontFamilyResolver,
            defaultDensity = density,
            cacheSize = 8,
        )
    val result =
        measurer.measure(
            text = text,
            style = style,
            softWrap = false,
            overflow = TextOverflow.Visible,
        )
    return result.size.width.toFloat() / density.density
}
