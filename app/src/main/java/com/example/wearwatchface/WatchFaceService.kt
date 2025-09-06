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
        val renderer = WatchFaceRenderer(
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
        // Example: bottom complication slot for calendar
        val calendarSlot = ComplicationSlot.createRoundRect(
            id = 0,
            bounds = RectF(0.35f, 0.80f, 0.65f, 0.95f), // bottom center
            boundsType = ComplicationSlotBoundsType.FRACTION,
            supportedTypes = listOf(ComplicationType.SHORT_TEXT, ComplicationType.RANGED_VALUE),
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

        // Add more slots as needed, e.g. left/right for steps/battery/weather

        return ComplicationSlotsManager(
            listOf(calendarSlot),
            currentUserStyleRepository
        )
    }
}