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

    override fun toString(): String = displayValue

    companion object {
        fun getValue(value: String): ChangeIntervals? =
            try { valueOf(value) } catch (_: IllegalArgumentException) { null }
    }
}
