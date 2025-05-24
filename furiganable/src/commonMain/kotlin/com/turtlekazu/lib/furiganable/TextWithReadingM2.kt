package com.turtlekazu.lib.furiganable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp

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
) {
    if (text.hasReadings() && showReadings) {

        val (textContent, inlineContent) =
            remember(text) {
                calculateAnnotatedStringM2(
                    textDataList = text.toTextData(),
                    showReadings = showReadings,
                    color = color,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    letterSpacing = letterSpacing,
                    textDecoration = textDecoration,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    style = style,
                )
            }

        val adjustedLineHeight = adjustedLineHeight(
            lineHeight = lineHeight,
            fontSize = fontSize,
            style = style,
        )

        Text(
            text = textContent,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = adjustedLineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = inlineContent,
            onTextLayout = onTextLayout ?: {},
            style = style.copy(
                lineHeight = adjustedLineHeight,
            ),
        )
    } else {
        Text(
            text = text,
            modifier = modifier,
            color = color,
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
            style = style,
        )
    }
}

private fun calculateAnnotatedStringM2(
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
            val width = (text.length.toDouble() + (text.length - 1) * 0.07).em

            appendInlineContent(text, text)
            inlineContent[text] = InlineTextContent(
                placeholder =
                    Placeholder(
                        width = width,
                        height = height,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                    ),
                children = {
                    val readingFontSize = mergedStyle.fontSize / 2

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextSpacingRemovedM2(
                            text = text,
                            style = mergedStyle,
                        )

                        Box(
                            modifier =
                                Modifier
                                    .graphicsLayer {
                                        translationY = -(readingFontSize.toPx() * getFuriganaPosition())
                                    },
                        ) {
                            if (showReadings) {
                                BasicText(
                                    modifier = Modifier.wrapContentWidth(unbounded = true),
                                    text = reading,
                                    style = style.copy(
                                        fontSize = readingFontSize,
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