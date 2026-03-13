package com.github.xtina.intellijthemerandomizer.startup

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.util.concurrency.AppExecutorUtil
import com.github.xtina.intellijthemerandomizer.ThemeRandomizer
import com.github.xtina.intellijthemerandomizer.settings.AppSettings
import com.github.xtina.intellijthemerandomizer.settings.ChangeIntervals
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Service(Service.Level.APP)
class ThemeSchedulerService : Disposable {

    private var scheduledFuture: ScheduledFuture<*>? = null
    private var started = false

    fun start() {
        if (started) return
        started = true
        reschedule()
    }

    fun reschedule() {
        scheduledFuture?.cancel(false)

        val settings = AppSettings.instance
        if (!settings.isChangeTheme) return

        val interval = ChangeIntervals.getValue(settings.interval) ?: ChangeIntervals.DAY
        val intervalMillis = interval.toMillis()
        val elapsed = System.currentTimeMillis() - settings.lastRandomizedMillis
        val initialDelay = maxOf(0L, intervalMillis - elapsed)

        scheduledFuture = AppExecutorUtil.getAppScheduledExecutorService()
            .scheduleWithFixedDelay(
                { ApplicationManager.getApplication().invokeLater { ThemeRandomizer.randomize() } },
                initialDelay,
                intervalMillis,
                TimeUnit.MILLISECONDS
            )
    }

    override fun dispose() {
        scheduledFuture?.cancel(false)
        scheduledFuture = null
        started = false
    }

    companion object {
        @JvmStatic
        val instance: ThemeSchedulerService
            get() = service()
    }
}
