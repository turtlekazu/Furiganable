package com.turtlekazu.furiganable.compose.core

/**
 * Returns a platform-specific compensation ratio to adjust spacing between base text and furigana (inline content).
 *
 * On **iOS**, inline content tends to appear more tightly packed compared to Android.
 * This function provides a correction factor to harmonize vertical spacing between platforms,
 * ensuring consistent visual alignment of furigana above base text.
 *
 * The returned value is a **ratio** relative to the main text’s font size.
 * For example, a return value of `0.1f` indicates that an extra spacing of 10% of the font size
 * should be added between the main text and its furigana.
 *
 * On **Android**, this typically returns `0f`, as the default inline content spacing is already adequate.
 *
 * Intended for internal use within layout calculations that involve furigana rendering.
 */
internal expect fun getFuriganaSpacingCompensation(): Float
