package com.example.wearwatchface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.SurfaceHolder
import java.util.*
import androidx.wear.watchface.*
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.complications.ComplicationSlotsManager
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.RenderParameters

/**
 * Custom renderer for analog hands and complications.
 */
class WatchFaceRenderer(
    surfaceHolder: SurfaceHolder,
    context: Context,
    watchState: WatchState,
    complicationSlotsManager: ComplicationSlotsManager,
    currentUserStyleRepository: CurrentUserStyleRepository
) : Renderer.CanvasRenderer2<WatchFaceRenderer.SharedAssets>(
    surfaceHolder, context, watchState, complicationSlotsManager, currentUserStyleRepository
) {
    private val bgRenderer = BackgroundRenderer(context)
    private val handPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 6f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        calendar: Calendar,
        renderParameters: RenderParameters,
        sharedAssets: SharedAssets?
    ) {
        // Draw custom background
        bgRenderer.draw(canvas, bounds)

        // Draw analog hour and minute hands
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()
        val radius = Math.min(centerX, centerY) * 0.95f

        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Compute angles
        val hourAngle = Math.toRadians(((hour % 12) + minute / 60f) * 30.0 - 90.0)
        val minAngle = Math.toRadians(minute * 6.0 - 90.0)
        val secAngle = Math.toRadians(second * 6.0 - 90.0)

        // Draw hour hand
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(hourAngle) * radius * 0.5).toFloat(),
            (centerY + Math.sin(hourAngle) * radius * 0.5).toFloat(),
            handPaint
        )

        // Draw minute hand
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(minAngle) * radius * 0.75).toFloat(),
            (centerY + Math.sin(minAngle) * radius * 0.75).toFloat(),
            handPaint
        )

        // Draw second hand (optional, thinner)
        val secondPaint = Paint(handPaint).apply {
            color = Color.RED
            strokeWidth = 3f
        }
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(secAngle) * radius * 0.85).toFloat(),
            (centerY + Math.sin(secAngle) * radius * 0.85).toFloat(),
            secondPaint
        )

        // Draw complications
        drawComplications(canvas, renderParameters)
    }

    private fun drawComplications(canvas: Canvas, renderParameters: RenderParameters) {
        for (slot in complicationSlotsManager.complicationSlots) {
            val drawable = ComplicationDrawable(context)
            val data = slot.complicationData
            if (data != null) {
                drawable.setComplicationData(data, false)
                drawable.bounds = slot.bounds
                drawable.draw(canvas, renderParameters)
            }
        }
    }

    class SharedAssets
