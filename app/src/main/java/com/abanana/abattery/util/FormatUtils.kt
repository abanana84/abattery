package com.abanana.abattery.util

fun Long.toReadableBytes(): String {
    return when {
        this >= 1_073_741_824L -> "%.1f GB".format(this / 1_073_741_824.0)
        this >= 1_048_576L -> "%.1f MB".format(this / 1_048_576.0)
        this >= 1_024L -> "%.1f KB".format(this / 1024.0)
        else -> "$this B"
    }
}

fun Long.toBytesPerSecToMbps(): String {
    val mbps = this * 8.0 / 1_000_000.0
    return "%.1f Mbps".format(mbps)
}

/** [estimated] = in-app throughput vs nominal mAh, not OEM BMS counter. */
fun formatCameraMpList(megapixels: List<Float>): String =
    if (megapixels.isEmpty()) {
        "N/A"
    } else {
        megapixels.joinToString(" + ") { mp ->
            if (mp >= 10f) "%.0f MP".format(mp) else "%.1f MP".format(mp)
        }
    }

fun formatBatteryCycleCount(
    count: Int?,
    estimated: Boolean,
    na: String,
    estimatedPattern: String,
): String = when {
    count == null -> na
    estimated -> estimatedPattern.format(count)
    else -> count.toString()
}

fun Long.uptimeToString(): String {
    val h = this / 3_600_000L
    val m = (this % 3_600_000L) / 60_000L
    val s = (this % 60_000L) / 1_000L
    return "%d:%02d:%02d".format(h, m, s)
}
