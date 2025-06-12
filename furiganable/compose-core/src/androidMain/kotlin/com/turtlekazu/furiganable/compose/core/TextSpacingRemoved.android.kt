package com.turtlekazu.furiganable.compose.core

import android.graphics.Typeface
import android.os.Build
import android.text.TextPaint
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal actual fun TextSpacingRemoved(
    text: String,
    color: Color,
    style: TextStyle,
    modifier: Modifier,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    letterSpacing: TextUnit,
    textDecoration: TextDecoration?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    overflow: TextOverflow,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
    onTextLayout: ((TextLayoutResult) -> Unit)?,
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val preMergedStyle =
        style.merge(
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign ?: TextAlign.Unspecified,
            lineHeight = lineHeight,
        )

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        BasicText(
            text,
            modifier,
            preMergedStyle,
            onTextLayout,
            overflow,
            softWrap,
            maxLines,
            minLines,
        )
    } else {
        val finalColor: Color =
            if (color.isSpecified) {
                color
            } else if (style.color.isSpecified) {
                style.color
            } else {
                DEFAULT_COLOR
            }

        val mergedStyle = preMergedStyle.merge(
            color = finalColor
        )

        val finalLineHeight = when {
            mergedStyle.lineHeight.isSpecified -> mergedStyle.lineHeight
            else -> DEFAULT_FONT_SIZE.sp * 1.2f
        }

        val finalFontSize = when {
            mergedStyle.fontSize.isSpecified -> mergedStyle.fontSize
            style.fontSize.isSpecified -> style.fontSize
            else -> DEFAULT_FONT_SIZE.sp
        }

        val finalLetterSpacing = when {
            mergedStyle.letterSpacing.isSpecified -> mergedStyle.letterSpacing
            else -> DEFAULT_LETTER_SPACING.sp
        }

        val resolver: FontFamily.Resolver = LocalFontFamilyResolver.current
        val typeface: Typeface =
            remember(resolver, mergedStyle) {
                resolver.resolve(
                    fontFamily = mergedStyle.fontFamily,
                    fontWeight = mergedStyle.fontWeight ?: FontWeight.Normal,
                    fontStyle = mergedStyle.fontStyle ?: FontStyle.Normal,
                    fontSynthesis = mergedStyle.fontSynthesis ?: FontSynthesis.All,
                )
            }.value as Typeface

        AndroidView(
            modifier = modifier,
            factory = {
                TextView(context).apply {
                    isFallbackLineSpacing = false
                    includeFontPadding = false
                }
            },
            update = { textView ->
                textView.setTextColor(finalColor.toArgb())
                textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    finalFontSize.value,
                )

                fun calcSingleLinePadding(
                    paint: TextPaint,
                    lineHeightPx: Float,
                ): Pair<Int, Int> {
                    val fm = paint.fontMetricsInt
                    val glyphBox = kotlin.math.abs(fm.ascent) + fm.descent
                    val extra = (lineHeightPx - glyphBox).coerceAtLeast(0f)
                    val topPad = (extra / 2f).toInt()
                    val bottomPad = extra.toInt() - topPad

                    return topPad to bottomPad
                }

                val lineHeightPx = finalLineHeight.value * density.density
                textView.lineHeight = (lineHeightPx).toInt()

                val (paddingTop, paddingBottom) =
                    calcSingleLinePadding(
                        paint = textView.paint,
                        lineHeightPx,
                    )
                textView.setPadding(0, paddingTop, 0, paddingBottom)
                textView.letterSpacing = finalLetterSpacing.value / finalFontSize.value

                textView.setMaxLines(maxLines)
                textView.setMinLines(minLines)
                textView.isSingleLine = !softWrap
                textView.textAlignment =
                    when (textAlign) {
                        TextAlign.Center -> TextView.TEXT_ALIGNMENT_CENTER
                        TextAlign.End -> TextView.TEXT_ALIGNMENT_TEXT_END
                        TextAlign.Left -> TextView.TEXT_ALIGNMENT_VIEW_START
                        TextAlign.Right -> TextView.TEXT_ALIGNMENT_VIEW_END
                        TextAlign.Justify -> TextView.TEXT_ALIGNMENT_TEXT_START
                        else -> TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                if (textDecoration != null) {
                    textView.paint.isUnderlineText =
                        textDecoration.contains(TextDecoration.Underline)
                    textView.paint.isStrikeThruText =
                        textDecoration.contains(TextDecoration.LineThrough)
                }
                textView.ellipsize =
                    when (overflow) {
                        TextOverflow.Ellipsis -> android.text.TextUtils.TruncateAt.END
                        else -> null
                    }
                textView.typeface = typeface

                textView.text = text
            },
        )
    }
}
