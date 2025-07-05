package com.turtlekazu.furiganable.compose.core

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.LeadingMarginSpan
import android.text.style.MetricAffectingSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlin.math.abs
import kotlin.math.roundToInt

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
            color = finalColor,
            lineHeight = if (preMergedStyle.lineHeight.isSpecified) {
                preMergedStyle.lineHeight
            } else DEFAULT_FONT_SIZE.sp * 1.2f
        )

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
                    includeFontPadding =
                        mergedStyle.platformStyle?.paragraphStyle?.includeFontPadding ?: false
                }
            },
            update = { textView ->
                textView.setTextColor(finalColor.toArgb())
                textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    finalFontSize.value,
                )
                textView.typeface = typeface
                mergedStyle.fontFeatureSettings?.let { textView.fontFeatureSettings = it }
                textView.letterSpacing = finalLetterSpacing.value / finalFontSize.value

                textView.applyMergedStyle(
                    rawText = text,
                    mergedStyle = mergedStyle,
                    density = density,
                )

                mergedStyle.localeList?.let { composeLocales ->
                    val tags = composeLocales.joinToString(",") { it.toLanguageTag() }
                    textView.textLocales = android.os.LocaleList.forLanguageTags(tags)
                }

                mergedStyle.textDecoration?.let { textDecoration ->
                    textView.paint.isUnderlineText =
                        textDecoration.contains(TextDecoration.Underline)
                    textView.paint.isStrikeThruText =
                        textDecoration.contains(TextDecoration.LineThrough)
                } ?: run {
                    textView.paint.isUnderlineText = false
                    textView.paint.isStrikeThruText = false
                }

                mergedStyle.shadow?.let { shadow ->
                    textView.setShadowLayer(
                        shadow.blurRadius,
                        shadow.offset.x,
                        shadow.offset.y,
                        shadow.color.toArgb(),
                    )
                }

                textView.textAlignment =
                    when (mergedStyle.textAlign) {
                        TextAlign.Center -> TextView.TEXT_ALIGNMENT_CENTER
                        TextAlign.End -> TextView.TEXT_ALIGNMENT_TEXT_END
                        TextAlign.Left -> TextView.TEXT_ALIGNMENT_VIEW_START
                        TextAlign.Right -> TextView.TEXT_ALIGNMENT_VIEW_END
                        TextAlign.Justify -> TextView.TEXT_ALIGNMENT_TEXT_START
                        else -> TextView.TEXT_ALIGNMENT_TEXT_START
                    }

                textView.textDirection = when (mergedStyle.textDirection) {
                    TextDirection.Rtl -> View.TEXT_DIRECTION_RTL
                    TextDirection.Ltr -> View.TEXT_DIRECTION_LTR
                    else -> View.TEXT_DIRECTION_INHERIT
                }

                textView.breakStrategy = when (mergedStyle.lineBreak) {
                    LineBreak.Simple -> Layout.BREAK_STRATEGY_SIMPLE
                    LineBreak.Paragraph -> Layout.BREAK_STRATEGY_HIGH_QUALITY
                    LineBreak.Heading -> Layout.BREAK_STRATEGY_BALANCED
                    else -> Layout.BREAK_STRATEGY_SIMPLE
                }

                textView.hyphenationFrequency = when (mergedStyle.hyphens) {
                    Hyphens.Auto -> Layout.HYPHENATION_FREQUENCY_NORMAL
                    else -> Layout.HYPHENATION_FREQUENCY_NONE
                }

                // Independent params from textStyle
                textView.setMaxLines(maxLines)
                textView.setMinLines(minLines)
                textView.isSingleLine = !softWrap
                textView.ellipsize =
                    when (overflow) {
                        TextOverflow.Ellipsis -> android.text.TextUtils.TruncateAt.END
                        else -> null
                    }
            },
        )
    }
}


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalTextApi::class)
fun TextView.applyMergedStyle(
    rawText: String,
    mergedStyle: TextStyle,
    density: Density,
) {
    mergedStyle.textGeometricTransform?.scaleX?.let { textScaleX = it }

    val spannable = SpannableString(rawText)

    val span = object : MetricAffectingSpan() {
        val shiftPx: Int? = mergedStyle.baselineShift?.let { shift ->
            val factor = when (shift) {
                BaselineShift.Superscript -> -0.5f
                BaselineShift.Subscript -> 0.5f
                else -> shift.multiplier
            }
            (textSize * factor).toInt()
        }
        val skewX = mergedStyle.textGeometricTransform?.skewX
        val stroke = (mergedStyle.drawStyle as? Stroke)

        override fun updateDrawState(p: TextPaint) {
            p.apply {
                shiftPx?.let { baselineShift += it }
                skewX?.let { textSkewX = it }
                stroke?.let {
                    style = Paint.Style.STROKE
                    strokeWidth = it.width
                }
            }
        }

        override fun updateMeasureState(p: TextPaint) = updateDrawState(p)
    }
    spannable.setSpan(span, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    mergedStyle.textIndent?.let { indent ->
        val firstPx = with(density) { indent.firstLine.toPx().toInt() }
        val restPx = with(density) { indent.restLine.toPx().toInt() }
        spannable.setSpan(
            LeadingMarginSpan.Standard(firstPx, restPx),
            0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    if (mergedStyle.background != Color.Unspecified) {
        spannable.setSpan(
            BackgroundColorSpan(mergedStyle.background.toArgb()),
            0,                   // start
            spannable.length,    // end
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    setText(spannable, TextView.BufferType.SPANNABLE)

    val lineHeightPx = with(density) { mergedStyle.lineHeight.toPx().roundToInt() }

    this.lineHeight = lineHeightPx

    println("lineHeightPx: $lineHeightPx density: ${density.density}")

    val (padTop, padBottom) =
        calcSingleLinePadding(
            paint = this.paint,
            lineHeightPx,
        )

    println("padTop: $padTop padBottom: $padBottom")
    setPadding(0, padTop, 0, padBottom)
}

private fun calcSingleLinePadding(
    paint: TextPaint,
    lineHeightPx: Int,
): Pair<Int, Int> {
    val fm = paint.fontMetricsInt
    val glyphBox = abs(fm.ascent) + fm.descent
    val extra = (lineHeightPx - glyphBox).coerceAtLeast(0)
    val topPad = (extra / 2f).toInt()
    val bottomPad = extra - topPad

    return topPad to bottomPad
}
