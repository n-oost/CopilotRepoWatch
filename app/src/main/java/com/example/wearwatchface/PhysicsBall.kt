package com.example.wearwatchface

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import kotlin.math.*

/**
 * Physics-based silver ball that responds to touch and gyroscope input.
 * Simulates realistic ball movement with friction, gravity, and collisions.
 */
class PhysicsBall(
    private val bounds: android.graphics.Rect
) {
    // Ball properties
    var x: Float = bounds.exactCenterX()
    var y: Float = bounds.exactCenterY()
    private var velocityX: Float = 0f
    private var velocityY: Float = 0f
    private val radius: Float = 15f
    private val mass: Float = 1f
    
    // Physics constants
    private val friction: Float = 0.98f
    private val bounceRestitution: Float = 0.7f
    private val gravityScale: Float = 200f
    
    // Touch interaction
    private var isBeingTouched: Boolean = false
    private var touchStartTime: Long = 0
    
    // Gyroscope forces
    private var gyroX: Float = 0f
    private var gyroY: Float = 0f
    
    // Rendering
    private val ballPaint = Paint().apply {
        isAntiAlias = true
    }
    
    // Trail effect for visual appeal
    private val trailPoints = mutableListOf<TrailPoint>()
    private val maxTrailPoints = 20
    
    private data class TrailPoint(val x: Float, val y: Float, val timestamp: Long)
    
    fun update(deltaTimeMs: Long) {
        val deltaTime = deltaTimeMs / 1000f
        
        // Apply gyroscope forces (convert to screen coordinates)
        velocityX += gyroX * gravityScale * deltaTime
        velocityY += gyroY * gravityScale * deltaTime
        
        // Update position
        x += velocityX * deltaTime
        y += velocityY * deltaTime
        
        // Apply friction
        velocityX *= friction
        velocityY *= friction
        
        // Collision detection with screen bounds
        handleBoundaryCollisions()
        
        // Update trail
        updateTrail()
    }
    
    private fun handleBoundaryCollisions() {
        // Left and right boundaries
        if (x - radius <= bounds.left) {
            x = bounds.left + radius
            velocityX = -velocityX * bounceRestitution
        } else if (x + radius >= bounds.right) {
            x = bounds.right - radius
            velocityX = -velocityX * bounceRestitution
        }
        
        // Top and bottom boundaries
        if (y - radius <= bounds.top) {
            y = bounds.top + radius
            velocityY = -velocityY * bounceRestitution
        } else if (y + radius >= bounds.bottom) {
            y = bounds.bottom - radius
            velocityY = -velocityY * bounceRestitution
        }
    }
    
    private fun updateTrail() {
        val currentTime = System.currentTimeMillis()
        trailPoints.add(TrailPoint(x, y, currentTime))
        
        // Remove old trail points
        while (trailPoints.size > maxTrailPoints) {
            trailPoints.removeAt(0)
        }
        
        // Remove trail points older than 1 second
        trailPoints.removeAll { currentTime - it.timestamp > 1000 }
    }
    
    fun onTouch(touchX: Float, touchY: Float, isDown: Boolean) {
        val distance = sqrt((touchX - x).pow(2) + (touchY - y).pow(2))
        
        if (isDown && distance <= radius * 2) {
            isBeingTouched = true
            touchStartTime = System.currentTimeMillis()
            
            // Stop the ball when touched
            velocityX *= 0.1f
            velocityY *= 0.1f
        } else if (!isDown && isBeingTouched) {
            isBeingTouched = false
            
            // Apply impulse based on touch duration and direction
            val touchDuration = System.currentTimeMillis() - touchStartTime
            val impulseStrength = min(touchDuration / 100f, 10f)
            
            val directionX = (touchX - x) / distance
            val directionY = (touchY - y) / distance
            
            velocityX += directionX * impulseStrength * 50
            velocityY += directionY * impulseStrength * 50
        }
    }
    
    fun onGyroscopeChanged(x: Float, y: Float, z: Float) {
        // Convert gyroscope readings to forces
        // Invert Y to match screen coordinates
        gyroX = x * 0.5f
        gyroY = -y * 0.5f
    }
    
    fun draw(canvas: Canvas) {
        // Draw trail
        drawTrail(canvas)
        
        // Draw ball with silver gradient
        drawBall(canvas)
    }
    
    private fun drawTrail(canvas: Canvas) {
        if (trailPoints.size < 2) return
        
        val trailPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb(100, 200, 200, 200)
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
        
        for (i in 1 until trailPoints.size) {
            val alpha = (i.toFloat() / trailPoints.size * 100).toInt()
            trailPaint.alpha = alpha
            
            canvas.drawLine(
                trailPoints[i-1].x, trailPoints[i-1].y,
                trailPoints[i].x, trailPoints[i].y,
                trailPaint
            )
        }
    }
    
    private fun drawBall(canvas: Canvas) {
        // Create silver gradient
        val gradient = RadialGradient(
            x - radius * 0.3f, y - radius * 0.3f, radius,
            intArrayOf(
                Color.argb(255, 245, 245, 245), // Highlight
                Color.argb(255, 192, 192, 192), // Mid silver
                Color.argb(255, 128, 128, 128)  // Shadow
            ),
            floatArrayOf(0f, 0.6f, 1f),
            Shader.TileMode.CLAMP
        )
        
        ballPaint.shader = gradient
        ballPaint.style = Paint.Style.FILL
        
        // Draw main ball
        canvas.drawCircle(x, y, radius, ballPaint)
        
        // Draw highlight for extra shine
        val highlightPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb(150, 255, 255, 255)
            style = Paint.Style.FILL
        }
        
        canvas.drawCircle(
            x - radius * 0.4f, 
            y - radius * 0.4f, 
            radius * 0.3f, 
            highlightPaint
        )
        
        // Draw subtle shadow/reflection if being touched
        if (isBeingTouched) {
            val touchPaint = Paint().apply {
                isAntiAlias = true
                color = Color.argb(50, 255, 255, 0)
                style = Paint.Style.STROKE
                strokeWidth = 4f
            }
            canvas.drawCircle(x, y, radius + 5f, touchPaint)
        }
    }
    
    // Get ball position for sand interaction
    fun getPosition(): Pair<Float, Float> = Pair(x, y)
    fun getRadius(): Float = radius
}