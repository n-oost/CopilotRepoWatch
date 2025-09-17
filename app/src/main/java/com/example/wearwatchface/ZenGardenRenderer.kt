package com.example.wearwatchface

import android.content.Context
import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Zen Garden renderer that creates sand patterns, rake marks, and responds to ball movement.
 */
class ZenGardenRenderer(context: Context) {
    
    // Sand texture properties
    private val sandPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    private val rakePaint = Paint().apply {
        isAntiAlias = true
        color = Color.argb(60, 139, 69, 19)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    
    private val ballTrackPaint = Paint().apply {
        isAntiAlias = true
        color = Color.argb(80, 101, 67, 33)
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }
    
    // Zen garden pattern data
    private var rakeLines = mutableListOf<RakeLine>()
    private var ballTracks = mutableListOf<BallTrack>()
    private val sandGradient: RadialGradient by lazy { createSandGradient() }
    
    // Pattern generation
    private var lastPatternUpdate = 0L
    private val patternUpdateInterval = 30000L // 30 seconds
    
    private data class RakeLine(
        val startX: Float, val startY: Float,
        val endX: Float, val endY: Float,
        val curvature: Float = 0f
    )
    
    private data class BallTrack(
        val points: MutableList<PointF>,
        val timestamp: Long
    )
    
    init {
        generateRakePattern()
    }
    
    private fun createSandGradient(): RadialGradient {
        return RadialGradient(
            0f, 0f, 500f,
            intArrayOf(
                Color.argb(255, 245, 235, 215), // Light sand
                Color.argb(255, 222, 184, 135), // Medium sand
                Color.argb(255, 205, 175, 149), // Darker sand
                Color.argb(255, 139, 115, 85)   // Deep sand
            ),
            floatArrayOf(0f, 0.3f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    
    fun drawOptimized(canvas: Canvas, bounds: Rect, ball: PhysicsBall? = null, isAmbient: Boolean = false) {
        // Simplified drawing for Pixel 1 performance
        if (isAmbient) {
            // Ultra-simple ambient mode
            drawSimpleBackground(canvas, bounds)
            return
        }
        
        // Regular optimized drawing
        drawSimpleBackground(canvas, bounds)
        
        // Only draw rake patterns every few frames for performance
        if (System.currentTimeMillis() % 100 < 50) {
            drawSimpleRakePattern(canvas, bounds)
        }
        
        // Update ball tracking (simplified)
        ball?.let { updateBallTrackingSimple(it) }
    }
    
    private fun drawSimpleBackground(canvas: Canvas, bounds: Rect) {
        // Simple sand color without complex gradients
        sandPaint.shader = null
        sandPaint.color = 0xFFF5EBDC.toInt() // Light sand color
        canvas.drawRect(bounds, sandPaint)
    }
    
    private fun drawSimpleRakePattern(canvas: Canvas, bounds: Rect) {
        // Draw simple concentric circles for zen pattern
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()
        val simplePaint = Paint().apply {
            isAntiAlias = true
            color = 0x20654321
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        
        for (i in 1..3) {
            val radius = i * 60f
            canvas.drawCircle(centerX, centerY, radius, simplePaint)
        }
    }
    
    private fun updateBallTrackingSimple(ball: PhysicsBall) {
        // Simplified ball tracking for Pixel 1
        // Just store the last position without complex trail rendering
    }
    
    private fun drawSandBackground(canvas: Canvas, bounds: Rect) {
        // Create subtle sand texture
        val gradient = RadialGradient(
            bounds.exactCenterX(), bounds.exactCenterY(),
            max(bounds.width(), bounds.height()) * 0.7f,
            intArrayOf(
                Color.argb(255, 245, 235, 215), // Light sand center
                Color.argb(255, 215, 205, 185), // Medium sand
                Color.argb(255, 185, 175, 155)  // Darker sand edges
            ),
            floatArrayOf(0f, 0.6f, 1f),
            Shader.TileMode.CLAMP
        )
        
        sandPaint.shader = gradient
        canvas.drawRect(bounds, sandPaint)
        
        // Add subtle sand grain texture
        drawSandGrain(canvas, bounds)
    }
    
    private fun drawSandGrain(canvas: Canvas, bounds: Rect) {
        val grainPaint = Paint().apply {
            isAntiAlias = false
            alpha = 30
        }
        
        val random = Random(42) // Fixed seed for consistent pattern
        
        for (i in 0 until 300) {
            val x = random.nextFloat() * bounds.width()
            val y = random.nextFloat() * bounds.height()
            val brightness = random.nextInt(50) + 205
            
            grainPaint.color = Color.rgb(brightness, brightness - 10, brightness - 20)
            canvas.drawPoint(x, y, grainPaint)
        }
    }
    
    private fun generateRakePattern() {
        rakeLines.clear()
        
        // Generate circular rake patterns (common in zen gardens)
        generateCircularPattern()
        
        // Generate parallel line patterns
        generateParallelPattern()
        
        // Generate spiral patterns
        generateSpiralPattern()
    }
    
    private fun generateCircularPattern() {
        val centerX = 200f
        val centerY = 150f
        val maxRadius = 80f
        val radiusStep = 15f
        
        var radius = radiusStep
        while (radius <= maxRadius) {
            val points = mutableListOf<PointF>()
            val steps = (radius * 0.5f).toInt().coerceAtLeast(8)
            
            for (i in 0 until steps) {
                val angle = (i.toFloat() / steps) * 2 * PI
                val x = centerX + cos(angle) * radius
                val y = centerY + sin(angle) * radius
                points.add(PointF(x.toFloat(), y.toFloat()))
            }
            
            // Create curved lines between points
            for (i in 0 until points.size) {
                val nextI = (i + 1) % points.size
                rakeLines.add(RakeLine(
                    points[i].x, points[i].y,
                    points[nextI].x, points[nextI].y
                ))
            }
            
            radius += radiusStep
        }
    }
    
    private fun generateParallelPattern() {
        val startX = 50f
        val endX = 350f
        val startY = 250f
        val lineSpacing = 20f
        
        for (i in 0 until 6) {
            val y = startY + i * lineSpacing
            val curvature = sin(i * 0.5f) * 10f
            
            rakeLines.add(RakeLine(startX, y, endX, y, curvature))
        }
    }
    
    private fun generateSpiralPattern() {
        val centerX = 300f
        val centerY = 300f
        val spiralTightness = 0.3f
        val maxRadius = 60f
        
        var angle = 0f
        var radius = 5f
        val points = mutableListOf<PointF>()
        
        while (radius < maxRadius) {
            val x = centerX + cos(angle) * radius
            val y = centerY + sin(angle) * radius
            points.add(PointF(x.toFloat(), y.toFloat()))
            
            angle += 0.3f
            radius += spiralTightness
        }
        
        // Create lines between consecutive points
        for (i in 0 until points.size - 1) {
            rakeLines.add(RakeLine(
                points[i].x, points[i].y,
                points[i + 1].x, points[i + 1].y
            ))
        }
    }
    
    private fun drawRakePatterns(canvas: Canvas, bounds: Rect) {
        for (line in rakeLines) {
            if (line.curvature == 0f) {
                // Straight line
                canvas.drawLine(line.startX, line.startY, line.endX, line.endY, rakePaint)
            } else {
                // Curved line
                drawCurvedLine(canvas, line)
            }
        }
    }
    
    private fun drawCurvedLine(canvas: Canvas, line: RakeLine) {
        val path = Path()
        path.moveTo(line.startX, line.startY)
        
        val midX = (line.startX + line.endX) / 2f
        val midY = (line.startY + line.endY) / 2f + line.curvature
        
        path.quadTo(midX, midY, line.endX, line.endY)
        canvas.drawPath(path, rakePaint)
    }
    
    private fun updateBallTracking(ball: PhysicsBall) {
        val (ballX, ballY) = ball.getPosition()
        val currentTime = System.currentTimeMillis()
        
        // Create or update current track
        if (ballTracks.isEmpty() || 
            ballTracks.last().points.isEmpty() ||
            distanceBetween(ballX, ballY, ballTracks.last().points.last()) > 10f) {
            
            if (ballTracks.isNotEmpty() && 
                currentTime - ballTracks.last().timestamp > 1000) {
                // Start new track if too much time has passed
                ballTracks.add(BallTrack(mutableListOf(PointF(ballX, ballY)), currentTime))
            } else if (ballTracks.isNotEmpty()) {
                // Add to existing track
                ballTracks.last().points.add(PointF(ballX, ballY))
            } else {
                // First track
                ballTracks.add(BallTrack(mutableListOf(PointF(ballX, ballY)), currentTime))
            }
        }
        
        // Remove old tracks (older than 30 seconds)
        ballTracks.removeAll { currentTime - it.timestamp > 30000 }
        
        // Limit number of tracks
        while (ballTracks.size > 5) {
            ballTracks.removeAt(0)
        }
    }
    
    private fun drawBallTracks(canvas: Canvas) {
        val currentTime = System.currentTimeMillis()
        
        for (track in ballTracks) {
            if (track.points.size < 2) continue
            
            // Calculate alpha based on age
            val age = currentTime - track.timestamp
            val alpha = (255 * (1f - age / 30000f)).toInt().coerceIn(0, 255)
            
            ballTrackPaint.alpha = alpha
            
            val path = Path()
            path.moveTo(track.points[0].x, track.points[0].y)
            
            for (i in 1 until track.points.size) {
                path.lineTo(track.points[i].x, track.points[i].y)
            }
            
            canvas.drawPath(path, ballTrackPaint)
        }
    }
    
    private fun distanceBetween(x1: Float, y1: Float, point: PointF): Float {
        return sqrt((x1 - point.x).pow(2) + (y1 - point.y).pow(2))
    }
    
    // Public method to add disturbance to sand (when ball moves through)
    fun addSandDisturbance(x: Float, y: Float, intensity: Float) {
        // This could be used to create dynamic sand displacement
        // For now, it's handled through ball tracking
    }
}