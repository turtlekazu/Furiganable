package com.turtlekazu.furiganable.compose.core

import android.graphics.Paint
import android.text.TextPaint
import android.text.style.LineHeightSpan
import androidx.annotation.Px
import androidx.compose.ui.text.style.LineHeightStyle
import kotlin.math.ceil

internal class ComposeLineHeightSpan(
    @Px private val lineHeight: Int,
    private val style: LineHeightStyle? = null,
    @Px private val shiftPx: Int = 0,
) : LineHeightSpan.WithDensity {
    override fun chooseHeight(
        text: CharSequence,
        start: Int,
        end: Int,
        spanstartv: Int,
        v: Int,
        fm: Paint.FontMetricsInt,
    ) = apply(text, start, end, fm, null)

    override fun chooseHeight(
        text: CharSequence,
        start: Int,
        end: Int,
        spanstartv: Int,
        v: Int,
        fm: Paint.FontMetricsInt,
        paint: TextPaint?,
    ) = apply(text, start, end, fm, paint)

    private fun apply(
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt,
        paint: TextPaint?,
    ) {
        val baseFm = paint?.fontMetricsInt ?: fm
        val origin = baseFm.descent - baseFm.ascent
        if (origin <= 0) return

        val align = style?.alignment ?: LineHeightStyle.Alignment.Proportional
        val trim = style?.trim ?: LineHeightStyle.Trim.Both

        val (topPad, botPad) =
            when (align) {
                LineHeightStyle.Alignment.Proportional -> proportionalPads(baseFm, origin)
                LineHeightStyle.Alignment.Center -> centerPads(origin)
                LineHeightStyle.Alignment.Top -> 0 to (lineHeight - origin)
                LineHeightStyle.Alignment.Bottom -> (lineHeight - origin) to 0
                else -> proportionalPads(fm, origin)
            }

        val paraStart = (start downTo 0).firstOrNull { text[it] == '\n' }?.plus(1) ?: 0
        val paraEnd = (end until text.length).firstOrNull { text[it] == '\n' } ?: text.length

        val isFirstLine = start == paraStart
        val isLastLine = end == paraEnd

        var finalTop =
            when (trim) {
                LineHeightStyle.Trim.None,
                LineHeightStyle.Trim.LastLineBottom,
                -> topPad
                LineHeightStyle.Trim.FirstLineTop,
                LineHeightStyle.Trim.Both,
                -> if (isFirstLine) 0 else topPad
                else -> if (isFirstLine) 0 else topPad
            }

        var finalBot =
            when (trim) {
                LineHeightStyle.Trim.None,
                LineHeightStyle.Trim.FirstLineTop,
                -> botPad
                LineHeightStyle.Trim.LastLineBottom,
                LineHeightStyle.Trim.Both,
                -> if (isLastLine) 0 else botPad
                else -> if (isLastLine) 0 else botPad
            }

        if (shiftPx < 0) { // Subscript
            if (isFirstLine) {
                finalTop += -shiftPx
            }
        } else if (shiftPx > 0) { // SuperScript
            if (isFirstLine) {
                finalBot += shiftPx
            } else {
                finalTop += -shiftPx
                finalBot += shiftPx
            }
        }

        fm.ascent = baseFm.ascent - finalTop
        fm.top = baseFm.top - finalTop
        fm.descent = baseFm.descent + finalBot
        fm.bottom = baseFm.bottom + finalBot
    }

    private fun proportionalPads(
        fm: Paint.FontMetricsInt,
        origin: Int,
    ): Pair<Int, Int> {
        val diff = lineHeight - origin
        if (diff == 0) return 0 to 0

        val ascentAbs = -fm.ascent
        val ratio = ascentAbs.toDouble() / origin

        val topExtra = ceil(diff * ratio).toInt()
        val botExtra = diff - topExtra
        return topExtra to botExtra
    }

    private fun centerPads(origin: Int): Pair<Int, Int> {
        val diff = lineHeight - origin
        if (diff == 0) return 0 to 0

        val topPad = diff / 2
        val botPad = diff - topPad

        return topPad to botPad
    }
}
