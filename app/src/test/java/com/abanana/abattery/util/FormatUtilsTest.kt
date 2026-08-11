package com.abanana.abattery.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatUtilsTest {

    @Test
    fun `missing cycle count uses fallback text`() {
        assertEquals(
            "N/A",
            formatBatteryCycleCount(null, estimated = false, na = "N/A", estimatedPattern = "~%d"),
        )
    }

    @Test
    fun `reported cycle count is shown as an exact value`() {
        assertEquals(
            "312",
            formatBatteryCycleCount(312, estimated = false, na = "N/A", estimatedPattern = "~%d"),
        )
    }

    @Test
    fun `estimated cycle count uses the localized pattern`() {
        assertEquals(
            "~27",
            formatBatteryCycleCount(27, estimated = true, na = "N/A", estimatedPattern = "~%d"),
        )
    }
}
