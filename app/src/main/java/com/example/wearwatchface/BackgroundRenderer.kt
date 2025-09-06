package com.example.wearwatchface

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

/**
 * Helper class to draw the background image.
 * Change the resource to swap the background.
 */
class BackgroundRenderer(
    context: Context,
    private val backgroundRes: Int = R.drawable.default_bg
) {
    private val paint = Paint()
    private val backgroundBitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources, backgroundRes
    )

    fun draw(canvas: Canvas, bounds: Rect) {
        val scaledBitmap = Bitmap.createScaledBitmap(
            backgroundBitmap,
            bounds.width(),
            bounds.height(),
            true
        )
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
    }
}