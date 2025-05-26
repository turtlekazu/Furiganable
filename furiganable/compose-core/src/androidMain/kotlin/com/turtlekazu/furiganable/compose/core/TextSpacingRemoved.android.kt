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
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun TextSpacingRemoved(
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

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        BasicText(
            text,
            modifier,
            style.merge(
                color = color,
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
    } else {
        val overrideColorOrUnspecified: Color =
            if (color.isSpecified) {
                color
            } else if (style.color.isSpecified) {
                style.color
            } else {
                color
            }

        val mergedStyle =
            style.merge(
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign ?: TextAlign.Unspecified,
                lineHeight = lineHeight,
            )

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
                textView.setTextColor(overrideColorOrUnspecified.toArgb())
                textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    mergedStyle.fontSize.value,
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

                val lineHeightPx = mergedStyle.lineHeight.value * density.density
                textView.lineHeight = (lineHeightPx).toInt()
                textView.letterSpacing =
                    mergedStyle.letterSpacing.value / mergedStyle.fontSize.value

                val (paddingTop, paddingBottom) =
                    calcSingleLinePadding(
                        paint = textView.paint,
                        lineHeightPx,
                    )
                textView.setPadding(0, paddingTop, 0, paddingBottom)

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
