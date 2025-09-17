package com.example.wearwatchface

import android.graphics.RectF
import android.service.wallpaper.WallpaperService
import android.content.ComponentName
import android.view.SurfaceHolder
import androidx.wear.watchface.*
import androidx.wear.watchface.complications.*
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.style.CurrentUserStyleRepository

/**
 * Main WatchFaceService that sets up complication slots and renderer.
 */
class WatchFaceService : WatchFaceService() {

    override fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val renderer = WatchFaceRendererOptimized(
            surfaceHolder,
            this,
            watchState,
            complicationSlotsManager,
            currentUserStyleRepository
        )
        return WatchFace(
            WatchFaceType.ANALOG,
            renderer
        )
    }

    override fun createComplicationSlotsManager(
        currentUserStyleRepository: CurrentUserStyleRepository
    ): ComplicationSlotsManager {
        
        // Top complication - Date/Calendar
        val topSlot = ComplicationSlot.createRoundRect(
            id = 0,
            bounds = RectF(0.35f, 0.05f, 0.65f, 0.20f), // top center
            boundsType = ComplicationSlotBoundsType.FRACTION,
            supportedTypes = listOf(ComplicationType.SHORT_TEXT, ComplicationType.LONG_TEXT),
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                ComponentName(
                    "com.google.android.calendar",
                    "com.google.android.clockwork.home.complications.CalendarComplicationProviderService"
                )
            ),
            configExtras = null,
            isEnabled = true,
            isInitiallyEnabled = true
        )

        // Left complication - Steps/Fitness
        val leftSlot = ComplicationSlot.createRoundRect(
            id = 1,
            bounds = RectF(0.05f, 0.35f, 0.25f, 0.65f), // left center
            boundsType = ComplicationSlotBoundsType.FRACTION,
            supportedTypes = listOf(ComplicationType.RANGED_VALUE, ComplicationType.SHORT_TEXT),
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                ComponentName(
                    "com.google.android.gms",
                    "com.google.android.gms.fitness.complications.StepsComplicationProviderService"
                )
            ),
            configExtras = null,
            isEnabled = true,
            isInitiallyEnabled = true
        )

        // Right complication - Battery
        val rightSlot = ComplicationSlot.createRoundRect(
            id = 2,
            bounds = RectF(0.75f, 0.35f, 0.95f, 0.65f), // right center
            boundsType = ComplicationSlotBoundsType.FRACTION,
            supportedTypes = listOf(ComplicationType.RANGED_VALUE, ComplicationType.SHORT_TEXT),
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                ComponentName(
                    "com.google.android.wearable.app",
                    "com.google.android.clockwork.complications.BatteryComplicationProviderService"
                )
            ),
            configExtras = null,
            isEnabled = true,
            isInitiallyEnabled = true
        )

        // Bottom complication - Weather
        val bottomSlot = ComplicationSlot.createRoundRect(
            id = 3,
            bounds = RectF(0.35f, 0.80f, 0.65f, 0.95f), // bottom center
            boundsType = ComplicationSlotBoundsType.FRACTION,
            supportedTypes = listOf(ComplicationType.SHORT_TEXT, ComplicationType.LONG_TEXT),
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                ComponentName(
                    "com.google.android.apps.weather",
                    "com.google.android.apps.weather.complications.WeatherComplicationProviderService"
                )
            ),
            configExtras = null,
            isEnabled = true,
            isInitiallyEnabled = true
        )

        return ComplicationSlotsManager(
            listOf(topSlot, leftSlot, rightSlot, bottomSlot),
            currentUserStyleRepository
        )
    }
}