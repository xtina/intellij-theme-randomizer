package com.github.xtina.intellijthemerandomizer.startup

import com.github.xtina.intellijthemerandomizer.settings.AppSettings
import com.github.xtina.intellijthemerandomizer.settings.ChangeIntervals
import com.intellij.ide.ui.LafManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.ui.UIUtil

class ThemeSchedulerServiceTest : BasePlatformTestCase() {

    private lateinit var service: ThemeSchedulerService
    private lateinit var settings: AppSettings

    override fun setUp() {
        super.setUp()
        service = ThemeSchedulerService.instance
        settings = AppSettings.instance
        settings.isChangeTheme = true
        settings.interval = ChangeIntervals.DAY.name
        settings.lastRandomizedMillis = 0L
    }

    override fun tearDown() {
        service.dispose()
        super.tearDown()
    }

    fun testStartIdempotent() {
        service.start()
        service.start()
    }

    fun testRescheduleWhenDisabled() {
        settings.isChangeTheme = false
        service.reschedule()
    }

    fun testRescheduleReplacesExisting() {
        settings.interval = ChangeIntervals.HOUR.name
        service.reschedule()
        settings.interval = ChangeIntervals.MINUTE.name
        service.reschedule()
    }

    fun testDisposeAfterStart() {
        service.start()
        service.dispose()
        // Should be restartable after dispose
        service.start()
    }

    fun testSchedulerFiresWhenExpired() {
        settings.interval = ChangeIntervals.MINUTE.name
        settings.lastRandomizedMillis = 0L

        service.reschedule()

        val hasThemes = LafManager.getInstance().installedThemes.toList().isNotEmpty()
        if (!hasThemes) return

        val deadline = System.currentTimeMillis() + 5000
        while (System.currentTimeMillis() < deadline) {
            UIUtil.dispatchAllInvocationEvents()
            if (settings.lastRandomizedMillis > 0L) break
            Thread.sleep(100)
        }

        assertTrue("Scheduler should have fired for expired interval", settings.lastRandomizedMillis > 0L)
    }

    fun testSchedulerDoesNotFireWhenDisabled() {
        settings.isChangeTheme = false
        settings.lastRandomizedMillis = 0L

        service.reschedule()
        Thread.sleep(1000)
        UIUtil.dispatchAllInvocationEvents()

        assertEquals("Scheduler should not fire when disabled", 0L, settings.lastRandomizedMillis)
    }
}
