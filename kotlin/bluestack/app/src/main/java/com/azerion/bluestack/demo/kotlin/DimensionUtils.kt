package com.azerion.bluestack.demo.kotlin

import android.content.Context

/**
 * Dimension conversion utilities for converting between DP and Pixels.
 */

object DimensionUtils {
    /**
     * Convert DP to Pixels
     */
    fun convertDpToPixel(context: Context, dp: Int): Int {
        val metrics = context.resources.displayMetrics
        return (dp * (metrics.densityDpi / 160f)).toInt()
    }
}