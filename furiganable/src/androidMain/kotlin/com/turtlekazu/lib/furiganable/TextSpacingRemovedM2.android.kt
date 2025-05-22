package com.turtlekazu.lib.furiganable

import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
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
actual fun TextSpacingRemovedM2(
    text: String,
    modifier: Modifier,
    color: Color,
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
    style: TextStyle,
) {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
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
    } else {
        val localContentColor = LocalContentColor.current
        val localContentAlpha = LocalContentAlpha.current
        val overrideColorOrUnspecified: Color = if (color.isSpecified) {
            color
        } else if (style.color.isSpecified) {
            style.color
        } else {
            localContentColor.copy(localContentAlpha)
        }

        val mergedStyle = style.merge(
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
        val typeface: Typeface = remember(resolver, mergedStyle) {
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
                    setTextColor(overrideColorOrUnspecified.toArgb())
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        mergedStyle.fontSize.value,
                    )

                    val lineSpacingPx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP,
                        mergedStyle.lineHeight.value - mergedStyle.fontSize.value,
                        context.resources.displayMetrics,
                    )
                    setLineSpacing(lineSpacingPx, 1f)

                    this.letterSpacing = mergedStyle.letterSpacing.value / mergedStyle.fontSize.value

                    setMaxLines(maxLines)
                    setMinLines(minLines)
                    this.isSingleLine = !softWrap
                    this.textAlignment = when (textAlign) {
                        TextAlign.Center -> TextView.TEXT_ALIGNMENT_CENTER
                        TextAlign.End -> TextView.TEXT_ALIGNMENT_TEXT_END
                        TextAlign.Left -> TextView.TEXT_ALIGNMENT_VIEW_START
                        TextAlign.Right -> TextView.TEXT_ALIGNMENT_VIEW_END
                        TextAlign.Justify -> TextView.TEXT_ALIGNMENT_TEXT_START
                        else -> TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                    if (textDecoration != null) {
                        this.paint.isUnderlineText =
                            textDecoration.contains(TextDecoration.Underline)
                        this.paint.isStrikeThruText =
                            textDecoration.contains(TextDecoration.LineThrough)
                    }
                    this.ellipsize = when (overflow) {
                        TextOverflow.Ellipsis -> android.text.TextUtils.TruncateAt.END
                        else -> null
                    }
                    this.typeface = typeface

                    this.text = text
                }
            },
        )
    }
}
