package com.example.wearwatchface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.SurfaceHolder
import android.view.MotionEvent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.*
import androidx.wear.watchface.*
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.complications.ComplicationSlotsManager
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.RenderParameters

/**
 * Pixel 1 optimized renderer for analog hands, complications, and simplified Zen Garden.
 * Optimized for lower-end hardware with reduced GPU usage and simplified physics.
 */
class WatchFaceRenderer(
    surfaceHolder: SurfaceHolder,
    private val context: Context,
    watchState: WatchState,
    complicationSlotsManager: ComplicationSlotsManager,
    currentUserStyleRepository: CurrentUserStyleRepository
) : Renderer.CanvasRenderer2<WatchFaceRenderer.SharedAssets>(
    surfaceHolder, context, watchState, complicationSlotsManager, currentUserStyleRepository
), SensorEventListener {
    
    // Simplified renderers for Pixel 1 performance
    private val zenGardenRenderer = ZenGardenRenderer(context)
    private var physicsBall: PhysicsBall? = null
    
    // Sensor management for gyroscope (with reduced sensitivity for Pixel 1)
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    
    // Touch handling
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isTouching = false
    
    // Timing for animation (reduced frequency for Pixel 1)
    private var lastUpdateTime = System.currentTimeMillis()
    private var frameSkipCounter = 0
    private val FRAME_SKIP_INTERVAL = 2 // Skip every other frame on Pixel 1
    
    // Paint for watch hands (optimized for Pixel 1)
    private val handPaint = Paint().apply {
        color = 0xFF654321.toInt() // Zen brown color
        strokeWidth = 4f // Reduced from 6f for better performance
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.FILL_AND_STROKE
    }
    
    private val secondHandPaint = Paint().apply {
        color = 0xFF8B4513.toInt() // Slightly lighter brown
        strokeWidth = 2f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }
    
    private val centerDotPaint = Paint().apply {
        color = 0xFF654321.toInt()
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    init {
        // Register gyroscope sensor with reduced frequency for Pixel 1
        gyroscopeSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        calendar: Calendar,
        renderParameters: RenderParameters,
        sharedAssets: SharedAssets?
    ) {
        // Frame skipping for Pixel 1 performance optimization
        frameSkipCounter++
        if (frameSkipCounter % FRAME_SKIP_INTERVAL != 0 && !renderParameters.drawMode.isInteractive) {
            return
        }
        
        // Initialize physics ball if not done yet
        if (physicsBall == null) {
            physicsBall = PhysicsBall(bounds)
        }
        
        // Calculate delta time for physics (with throttling for Pixel 1)
        val currentTime = System.currentTimeMillis()
        val deltaTime = minOf(currentTime - lastUpdateTime, 33L) // Cap at 30fps
        lastUpdateTime = currentTime
        
        // Update physics ball with reduced frequency
        if (frameSkipCounter % 2 == 0) {
            physicsBall?.update(deltaTime)
        }
        
        // Draw zen garden background with simplified rendering
        zenGardenRenderer.drawOptimized(canvas, bounds, physicsBall, renderParameters.drawMode.isAmbient)

        // Draw analog hands
        drawWatchHands(canvas, bounds, calendar, renderParameters.drawMode.isAmbient)

        // Draw physics ball (simplified for Pixel 1)
        if (!renderParameters.drawMode.isAmbient) {
            physicsBall?.drawSimplified(canvas)
        }
        
        // Draw complications with optimizations
        drawComplicationsOptimized(canvas, renderParameters)
        
        // Reduced invalidation frequency for Pixel 1
        if (renderParameters.drawMode.isInteractive && frameSkipCounter % 3 == 0) {
            postInvalidate()
        }
    }

    private fun drawWatchHands(canvas: Canvas, bounds: Rect, calendar: Calendar, isAmbient: Boolean) {
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        
        // Get time components
        val hour = calendar.get(Calendar.HOUR_OF_DAY) % 12
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        
        // Calculate angles
        val hourAngle = (hour * 30f) + (minute * 0.5f) - 90f
        val minuteAngle = (minute * 6f) - 90f
        val secondAngle = (second * 6f) - 90f
        
        // Hand lengths (optimized for Pixel 1 smaller screen)
        val hourHandLength = bounds.width() * 0.25f
        val minuteHandLength = bounds.width() * 0.35f
        val secondHandLength = bounds.width() * 0.4f
        
        // Draw hour hand
        val hourEndX = centerX + hourHandLength * Math.cos(Math.toRadians(hourAngle.toDouble())).toFloat()
        val hourEndY = centerY + hourHandLength * Math.sin(Math.toRadians(hourAngle.toDouble())).toFloat()
        canvas.drawLine(centerX, centerY, hourEndX, hourEndY, handPaint)
        
        // Draw minute hand
        val minuteEndX = centerX + minuteHandLength * Math.cos(Math.toRadians(minuteAngle.toDouble())).toFloat()
        val minuteEndY = centerY + minuteHandLength * Math.sin(Math.toRadians(minuteAngle.toDouble())).toFloat()
        canvas.drawLine(centerX, centerY, minuteEndX, minuteEndY, handPaint)
        
        // Draw second hand (only in interactive mode)
        if (!isAmbient) {
            val secondEndX = centerX + secondHandLength * Math.cos(Math.toRadians(secondAngle.toDouble())).toFloat()
            val secondEndY = centerY + secondHandLength * Math.sin(Math.toRadians(secondAngle.toDouble())).toFloat()
            canvas.drawLine(centerX, centerY, secondEndX, secondEndY, secondHandPaint)
        }
        
        // Draw center dot
        canvas.drawCircle(centerX, centerY, 8f, centerDotPaint)
    }

    private fun drawComplicationsOptimized(canvas: Canvas, renderParameters: RenderParameters) {
        for ((_, complication) in complicationSlotsManager.complicationSlots) {
            if (complication.enabled) {
                val drawable = ComplicationDrawable.getDrawable(context)
                drawable?.let {
                    it.setCurrentTime(Instant.ofEpochMilli(System.currentTimeMillis()))
                    it.draw(canvas, System.currentTimeMillis())
                }
            }
        }
    }

    // Touch handling optimized for Pixel 1
    override fun onTapCommand(
        tapType: Int,
        x: Int,
        y: Int,
        tapTimeMillis: Long
    ) {
        when (tapType) {
            TapType.TAP -> {
                // Handle tap on physics ball with reduced sensitivity
                physicsBall?.let { ball ->
                    val distance = Math.sqrt(
                        Math.pow((x - ball.x).toDouble(), 2.0) +
                        Math.pow((y - ball.y).toDouble(), 2.0)
                    )
                    if (distance < ball.radius * 2) { // Increased tap area for Pixel 1
                        ball.applyForce(
                            (x - ball.x) * 0.1f, // Reduced force for smoother interaction
                            (y - ball.y) * 0.1f
                        )
                    }
                }
            }
        }
    }

    // Sensor handling with reduced sensitivity for Pixel 1
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            physicsBall?.let { ball ->
                // Apply gyroscope influence with reduced sensitivity for Pixel 1
                val sensitivity = 0.3f // Reduced from typical 0.5f
                ball.applyForce(
                    -event.values[1] * sensitivity,
                    event.values[0] * sensitivity
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes if needed
    }

    override fun createSharedAssets(): SharedAssets {
        return SharedAssets()
    }

    class SharedAssets : Renderer.SharedAssets {
        override fun onDestroy() {
            // Clean up shared assets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister sensor listener
        sensorManager.unregisterListener(this)
    }
}