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
 * Custom renderer for analog hands, complications, and Zen Garden with physics ball.
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
    
    // Renderers
    private val zenGardenRenderer = ZenGardenRenderer(context)
    private var physicsBall: PhysicsBall? = null
    
    // Sensor management for gyroscope
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    
    // Touch handling
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isTouching = false
    
    // Timing for animation
    private var lastUpdateTime = System.currentTimeMillis()
    
    // Paint for watch hands
    private val handPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 6f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    
    init {
        // Register gyroscope sensor
        gyroscopeSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        calendar: Calendar,
        renderParameters: RenderParameters,
        sharedAssets: SharedAssets?
    ) {
        // Initialize physics ball if not done yet
        if (physicsBall == null) {
            physicsBall = PhysicsBall(bounds)
        }
        
        // Calculate delta time for physics
        val currentTime = System.currentTimeMillis()
        val deltaTime = currentTime - lastUpdateTime
        lastUpdateTime = currentTime
        
        // Update physics ball
        physicsBall?.update(deltaTime)
        
        // Draw zen garden background with ball tracks
        zenGardenRenderer.draw(canvas, bounds, physicsBall)

        // Draw analog hour and minute hands
        drawWatchHands(canvas, bounds, calendar)

        // Draw physics ball
        physicsBall?.draw(canvas)
        
        // Draw complications
        drawComplications(canvas, renderParameters)
        
        // Schedule next frame for smooth animation
        postInvalidate()
    }
    
    private fun drawWatchHands(canvas: Canvas, bounds: Rect, calendar: Calendar) {
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()
        val radius = kotlin.math.min(centerX, centerY) * 0.8f // Reduced to avoid interfering with complications

        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Compute angles
        val hourAngle = Math.toRadians(((hour % 12) + minute / 60f) * 30.0 - 90.0)
        val minAngle = Math.toRadians(minute * 6.0 - 90.0)
        val secAngle = Math.toRadians(second * 6.0 - 90.0)

        // Draw hour hand (shorter, thicker)
        val hourPaint = Paint(handPaint).apply {
            strokeWidth = 8f
            color = Color.argb(200, 101, 67, 33) // Zen brown color
        }
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(hourAngle) * radius * 0.5).toFloat(),
            (centerY + Math.sin(hourAngle) * radius * 0.5).toFloat(),
            hourPaint
        )

        // Draw minute hand (longer, medium thickness)  
        val minutePaint = Paint(handPaint).apply {
            strokeWidth = 6f
            color = Color.argb(200, 139, 69, 19) // Darker zen brown
        }
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(minAngle) * radius * 0.75).toFloat(),
            (centerY + Math.sin(minAngle) * radius * 0.75).toFloat(),
            minutePaint
        )

        // Draw second hand (thinner, subtle)
        val secondPaint = Paint(handPaint).apply {
            color = Color.argb(150, 160, 82, 45) // Subtle brown
            strokeWidth = 2f
        }
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(secAngle) * radius * 0.85).toFloat(),
            (centerY + Math.sin(secAngle) * radius * 0.85).toFloat(),
            secondPaint
        )
        
        // Draw center dot
        val centerPaint = Paint().apply {
            color = Color.argb(180, 101, 67, 33)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(centerX, centerY, 6f, centerPaint)
    }

    
    private fun drawComplications(canvas: Canvas, renderParameters: RenderParameters) {
        for ((_, slot) in complicationSlotsManager.complicationSlots) {
            val drawable = ComplicationDrawable(context)
            slot.complicationData.value?.let { data ->
                drawable.setComplicationData(data, false)
                drawable.bounds = slot.bounds
                drawable.draw(canvas, renderParameters)
            }
        }
    }
    
    // Touch handling for ball interaction
    fun handleTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isTouching = true
                physicsBall?.onTouch(lastTouchX, lastTouchY, true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                physicsBall?.onTouch(lastTouchX, lastTouchY, false)
                isTouching = false
            }
        }
        
        // Request redraw for smooth animation
        postInvalidate()
    }
    
    // Gyroscope sensor handling
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            // Pass gyroscope data to physics ball
            physicsBall?.onGyroscopeChanged(event.values[0], event.values[1], event.values[2])
            
            // Trigger redraw for smooth animation
            postInvalidate()
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Unregister sensor listener to save battery
        sensorManager.unregisterListener(this)
    }

    class SharedAssets
