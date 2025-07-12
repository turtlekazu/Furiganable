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
import android.util.LayoutDirection
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextLayoutInput
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.P)
@Composable
internal fun CustomAndroidViewText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null, // TODO: handle text layout
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    color: Color,
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val finalColor: Color =
        if (color.isSpecified) {
            color
        } else if (style.color.isSpecified) {
            style.color
        } else {
            DEFAULT_COLOR
        }

    val mergedStyle = style.merge(
        color = finalColor,
        lineHeight = if (style.lineHeight.isSpecified) {
            style.lineHeight
        } else DEFAULT_FONT_SIZE.sp * 1.2f,
        fontSize = if (style.fontSize.isSpecified) {
            style.fontSize
        } else DEFAULT_FONT_SIZE.sp,
        letterSpacing = if (style.letterSpacing.isSpecified) {
            style.letterSpacing
        } else DEFAULT_LETTER_SPACING.sp,
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
                includeFontPadding =
                    mergedStyle.platformStyle?.paragraphStyle?.includeFontPadding ?: false
            }
        },
        update = { textView ->
            textView.setTextColor(finalColor.toArgb())
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                mergedStyle.fontSize.value,
            )
            textView.typeface = typeface
            mergedStyle.fontFeatureSettings?.let { textView.fontFeatureSettings = it }
            textView.letterSpacing =
                mergedStyle.letterSpacing.value / mergedStyle.fontSize.value

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

            // TODO: handle style.textMotion

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

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalTextApi::class)
private fun TextView.applyMergedStyle(
    rawText: String,
    mergedStyle: TextStyle,
    density: Density,
) {
    mergedStyle.textGeometricTransform?.scaleX?.let { textScaleX = it }

    val spannable = SpannableString(rawText)

    val commonSpan = object : MetricAffectingSpan() {
        val skewX = mergedStyle.textGeometricTransform?.skewX
        val stroke = mergedStyle.drawStyle as? Stroke

        override fun updateDrawState(p: TextPaint) {
            skewX?.let { p.textSkewX = it }
            stroke?.let {
                p.style = Paint.Style.STROKE
                p.strokeWidth = it.width
            }
        }

        override fun updateMeasureState(p: TextPaint) = updateDrawState(p)
    }
    spannable.setSpan(commonSpan, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    mergedStyle.textIndent?.let { indent ->
        val firstPx = with(density) { indent.firstLine.toPx().toInt() }
        val restPx = with(density) { indent.restLine.toPx().toInt() }
        val leadingSpan = LeadingMarginSpan.Standard(firstPx, restPx)
        spannable.setSpan(leadingSpan, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    if (mergedStyle.background != Color.Unspecified) {
        spannable.setSpan(
            BackgroundColorSpan(mergedStyle.background.toArgb()),
            0,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    val shiftPx = mergedStyle.baselineShift?.let { shift ->
        val factor = when (shift) {
            BaselineShift.Superscript -> 0.5f
            BaselineShift.Subscript -> -0.5f
            else -> shift.multiplier
        }
        (textSize * factor).toInt()
    } ?: 0

    val lineHeightPx = with(density) { mergedStyle.lineHeight.toPx().roundToInt() }
    val composeSpan = ComposeLineHeightSpan(lineHeightPx, mergedStyle.lineHeightStyle, shiftPx)
    spannable.setSpan(composeSpan, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    setText(spannable, TextView.BufferType.SPANNABLE)
}
