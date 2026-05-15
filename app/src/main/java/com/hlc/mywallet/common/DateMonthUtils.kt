package com.hlc.mywallet.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateMonthUtils {

    private const val DEFAULT_MONTH_COUNT = 12
    private const val YEAR_MONTH_PATTERN = "yyyy-MM"

    fun getCurrentYearMonth(timeInMillis: Long = System.currentTimeMillis()): String {
        return createFormatter().format(timeInMillis)
    }

    fun getRecentYearMonths(
        count: Int = DEFAULT_MONTH_COUNT,
        timeInMillis: Long = System.currentTimeMillis()
    ): List<String> {
        if (count <= 0) {
            return emptyList()
        }
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return buildList(count) {
            repeat(count) {
                add(createFormatter().format(calendar.time))
                calendar.add(Calendar.MONTH, -1)
            }
        }
    }

    private fun createFormatter(): SimpleDateFormat {
        return SimpleDateFormat(YEAR_MONTH_PATTERN, Locale.getDefault())
    }
}
