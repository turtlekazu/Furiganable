package com.turtlekazu.lib.furiganable

import android.os.Build
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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

    fun Float.toPx() = this * context.resources.displayMetrics.density

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
        AndroidView(
            modifier = modifier
                .background(Color.Blue.copy(0.3f)),
            factory = {
                TextView(context).apply {
                    isFallbackLineSpacing = false
                    includeFontPadding = false
//                setTextColor(style.color.toArgb())
                    setTextSize(
                        android.util.TypedValue.COMPLEX_UNIT_PX,
                        style.fontSize.value.toPx()
                    )
                    setLineSpacing(lineHeight.value.toPx(), 1.0f)
                    setMaxLines(maxLines)
                    setMinLines(minLines)
                    isSingleLine = !softWrap
                    textAlignment = when (textAlign) {
                        TextAlign.Center -> TextView.TEXT_ALIGNMENT_CENTER
                        TextAlign.End -> TextView.TEXT_ALIGNMENT_TEXT_END
                        TextAlign.Left -> TextView.TEXT_ALIGNMENT_VIEW_START
                        TextAlign.Right -> TextView.TEXT_ALIGNMENT_VIEW_END
                        TextAlign.Justify -> TextView.TEXT_ALIGNMENT_TEXT_START
                        else -> TextView.TEXT_ALIGNMENT_TEXT_START
                    }

                    if (textDecoration != null) {
                        paint.isUnderlineText = textDecoration.contains(TextDecoration.Underline)
                        paint.isStrikeThruText = textDecoration.contains(TextDecoration.LineThrough)
                    }

                    this.ellipsize = when (overflow) {
                        TextOverflow.Ellipsis -> android.text.TextUtils.TruncateAt.END
                        else -> null
                    }

                    setText(text)
                }
            },
        )
    }
}
