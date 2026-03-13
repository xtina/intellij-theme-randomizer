package com.github.xtina.intellijthemerandomizer.settings

enum class ChangeIntervals(val displayValue: String) {
    MINUTE("Every Minute"),
    FIVE_MINUTES("Every 5 minutes"),
    TEN_MINUTES("Every 10 minutes"),
    FIFTEEN_MINUTES("Every 15 minutes"),
    THIRTY_MINUTES("Every 30 Minutes"),
    HOUR("Every Hour"),
    DAY("Every day"),
    TWO_DAYS("Every other day"),
    WEEK("Every 7 days");

    fun toMillis(): Long = when (this) {
        MINUTE -> 60_000L
        FIVE_MINUTES -> 5 * 60_000L
        TEN_MINUTES -> 10 * 60_000L
        FIFTEEN_MINUTES -> 15 * 60_000L
        THIRTY_MINUTES -> 30 * 60_000L
        HOUR -> 3_600_000L
        DAY -> 86_400_000L
        TWO_DAYS -> 2 * 86_400_000L
        WEEK -> 7 * 86_400_000L
    }

    override fun toString(): String = displayValue

    companion object {
        fun getValue(value: String): ChangeIntervals? =
            try { valueOf(value) } catch (_: IllegalArgumentException) { null }
    }
}
