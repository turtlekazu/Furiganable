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
import android.text.style.LineHeightSpan
import android.text.style.MetricAffectingSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.Px
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
import androidx.compose.ui.text.TextPainter.paint
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
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
import kotlin.math.ceil
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
            } else DEFAULT_FONT_SIZE.sp * 1.2f,
            fontSize = if (preMergedStyle.fontSize.isSpecified) {
                preMergedStyle.fontSize
            } else DEFAULT_FONT_SIZE.sp,
            letterSpacing = if (preMergedStyle.letterSpacing.isSpecified) {
                preMergedStyle.letterSpacing
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
                textView.letterSpacing = mergedStyle.letterSpacing.value / mergedStyle.fontSize.value

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

    val commonSpan = object : MetricAffectingSpan() {
        val skewX  = mergedStyle.textGeometricTransform?.skewX
        val stroke = mergedStyle.drawStyle as? Stroke

        override fun updateDrawState(p: TextPaint) {
            skewX?.let { p.textSkewX = it }
            stroke?.let {
                p.style       = Paint.Style.STROKE
                p.strokeWidth = it.width
            }
        }
        override fun updateMeasureState(p: TextPaint) = updateDrawState(p)
    }
    // MetricAffectingSpan apply to all lines
    spannable.setSpan(commonSpan, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    mergedStyle.textIndent?.let { indent ->
        val firstPx = with(density) { indent.firstLine.toPx().toInt() }
        val restPx = with(density) { indent.restLine.toPx().toInt() }
        val leadingSpan = LeadingMarginSpan.Standard(firstPx, restPx)
        // LeadingMarginSpan apply to all lines
        spannable.setSpan(leadingSpan, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    if (mergedStyle.background != Color.Unspecified) {
        // BackgroundColorSpan apply to all lines
        spannable.setSpan(
            BackgroundColorSpan(mergedStyle.background.toArgb()),
            0,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    val shiftPx = mergedStyle.baselineShift?.let { shift ->
        val factor = when (shift) {
            BaselineShift.Superscript -> -0.5f
            BaselineShift.Subscript   ->  0.5f
            else                      ->  shift.multiplier
        }
        (textSize * factor).toInt()
    } ?: 0

    /*------------------ baselineShiftSpan をここで貼る ------------------*/
    if (shiftPx != 0) {
        val lastStart = rawText.lastIndexOf('\n').let { if (it == -1) 0 else it + 1 }
        spannable.setSpan(
            object : MetricAffectingSpan() {
                override fun updateDrawState(p: TextPaint)  { p.baselineShift += shiftPx }
                override fun updateMeasureState(p: TextPaint) = updateDrawState(p)
            },
            lastStart, spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    /*-------------------------------------------------------------------*/

    /* ------------ 行高スパンに shiftPx を渡す ------------------------- */
    val lineHeightPx = with(density) { mergedStyle.lineHeight.toPx().roundToInt() }
    val composeSpan  = ComposeLineHeightSpan(lineHeightPx, mergedStyle.lineHeightStyle, shiftPx)
    spannable.setSpan(composeSpan, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    /*-------------------------------------------------------------------*/

    setText(spannable, TextView.BufferType.SPANNABLE)
}

fun createLineHeightSpan(
    lineHeightPx: Int,
    lineHeightStyle: LineHeightStyle?,
    shiftPx: Int,
): LineHeightSpan =
    ComposeLineHeightSpan(lineHeightPx, lineHeightStyle, shiftPx)

class ComposeLineHeightSpan(
    @Px private val lineHeight: Int,
    private val style: LineHeightStyle? = null,
    @Px private val shiftPx: Int = 0,
) : LineHeightSpan.WithDensity {

    /* ─────────── chooseHeight ─────────── */

    override fun chooseHeight(
        text: CharSequence, start: Int, end: Int,
        spanstartv: Int, v: Int,
        fm: Paint.FontMetricsInt
    ) = apply(text, start, end, fm, null)

    override fun chooseHeight(
        text: CharSequence, start: Int, end: Int,
        spanstartv: Int, v: Int,
        fm: Paint.FontMetricsInt, paint: TextPaint?
    ) = apply(text, start, end, fm, paint)

    /* ─────────── main ─────────── */

    private fun apply(
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt,
        paint: TextPaint?,
    ) {
        val baseFm = paint?.fontMetricsInt ?: fm   // paint が null の場合は fallback
        val origin = baseFm.descent - baseFm.ascent
        if (origin <= 0) return

        val align = style?.alignment ?: LineHeightStyle.Alignment.Proportional
        val trim  = style?.trim      ?: LineHeightStyle.Trim.Both

        val (topPad, botPad) = when (align) {
            LineHeightStyle.Alignment.Proportional -> proportionalPads(fm, origin)
            LineHeightStyle.Alignment.Center       -> centerPads(fm, origin)
            LineHeightStyle.Alignment.Top          -> 0          to (lineHeight - origin)
            LineHeightStyle.Alignment.Bottom       -> (lineHeight - origin) to 0
            else -> proportionalPads(fm, origin)
        }
        println("isFirstLine: topPad: $topPad, botPad: $botPad, origin: $origin lineHeight: $lineHeight")

        // ── Trim 処理 ──
        val paraStart = (start downTo 0).firstOrNull { text[it] == '\n' }?.plus(1) ?: 0
        val paraEnd   = (end until text.length).firstOrNull { text[it] == '\n' } ?: text.length

        val isFirstLine = start == paraStart
        val isLastLine  = end   == paraEnd

        println("isFirstLine: $isFirstLine, isLastLine: $isLastLine")

        var finalTop = when (trim) {
            LineHeightStyle.Trim.None,
            LineHeightStyle.Trim.LastLineBottom -> topPad
            LineHeightStyle.Trim.FirstLineTop,
            LineHeightStyle.Trim.Both          -> if (isFirstLine) 0 else topPad
            else -> if (isFirstLine) 0 else topPad
        }

        var finalBot = when (trim) {
            LineHeightStyle.Trim.None,
            LineHeightStyle.Trim.FirstLineTop -> botPad
            LineHeightStyle.Trim.LastLineBottom,
            LineHeightStyle.Trim.Both         -> if (isLastLine) 0 else botPad
            else -> if (isLastLine) 0 else botPad
        }

        if (shiftPx < 0 && isFirstLine) {              // 上方向（Superscript）
            finalTop += -shiftPx        // 上側余白を増やす
        } else if (shiftPx > 0 && isLastLine) {       // 下方向（Subscript）
            finalBot += shiftPx         // 下側余白を増やす
        }

        println("isFirstLine finalTop: $finalTop, finalBot: $finalBot")

        fm.ascent  = baseFm.ascent  - finalTop
        fm.top     = baseFm.top     - finalTop
        fm.descent = baseFm.descent + finalBot
        fm.bottom  = baseFm.bottom  + finalBot
    }

    /* ─────────── Alignment = Proportional ─────────── */

    private fun proportionalPads(
        fm: Paint.FontMetricsInt,
        origin: Int            // ascent+descent (px, 正数)
    ): Pair<Int, Int> {
        val diff = lineHeight - origin      // ±差分
        if (diff == 0) return 0 to 0

        val ascentAbs = -fm.ascent          // baseline 上側
        val ratio     = ascentAbs.toDouble() / origin

        // 差分を比率で配分（上側は必ず切り上げ）
        val topExtra  = ceil(diff * ratio).toInt()
        val botExtra  = diff - topExtra
        return topExtra to botExtra
    }

    /* ─────────── Alignment = Center ─────────── */
    private fun centerPads(
        fm: Paint.FontMetricsInt,
        origin: Int      // ascent + descent
    ): Pair<Int, Int> {
        val diff = lineHeight - origin          // 余剰 (必ず正数でここに来る)
        if (diff == 0) return 0 to 0            // 行高が既に一致

        val topPad = diff / 2                   // 切り捨て
        val botPad = diff - topPad              // 残りを下側へ (diff が奇数なら +1px)

        return topPad to botPad
    }
}