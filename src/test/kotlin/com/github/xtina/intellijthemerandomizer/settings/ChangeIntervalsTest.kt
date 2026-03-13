package com.github.xtina.intellijthemerandomizer.settings

import junit.framework.TestCase

class ChangeIntervalsTest : TestCase() {

    fun testToMillis() {
        assertEquals(60_000L, ChangeIntervals.MINUTE.toMillis())
        assertEquals(300_000L, ChangeIntervals.FIVE_MINUTES.toMillis())
        assertEquals(600_000L, ChangeIntervals.TEN_MINUTES.toMillis())
        assertEquals(900_000L, ChangeIntervals.FIFTEEN_MINUTES.toMillis())
        assertEquals(1_800_000L, ChangeIntervals.THIRTY_MINUTES.toMillis())
        assertEquals(3_600_000L, ChangeIntervals.HOUR.toMillis())
        assertEquals(86_400_000L, ChangeIntervals.DAY.toMillis())
        assertEquals(172_800_000L, ChangeIntervals.TWO_DAYS.toMillis())
        assertEquals(604_800_000L, ChangeIntervals.WEEK.toMillis())
    }

    fun testToMillisOrdering() {
        val entries = ChangeIntervals.entries
        for (i in 0 until entries.size - 1) {
            assertTrue(
                "${entries[i].name} should be less than ${entries[i + 1].name}",
                entries[i].toMillis() < entries[i + 1].toMillis()
            )
        }
    }

    fun testGetValueValid() {
        for (interval in ChangeIntervals.entries) {
            assertEquals(interval, ChangeIntervals.getValue(interval.name))
        }
    }

    fun testGetValueInvalid() {
        assertNull(ChangeIntervals.getValue("NONEXISTENT"))
    }
}
