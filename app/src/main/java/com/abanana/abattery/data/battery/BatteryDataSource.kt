package com.abanana.abattery.data.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.abanana.abattery.domain.model.BatteryChargeStatus
import com.abanana.abattery.domain.model.BatteryHealthState
import com.abanana.abattery.domain.model.BatteryInfo
import com.abanana.abattery.domain.model.PowerPlugType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class BatteryDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private companion object {
        private const val PREFS_CYCLE_TRACK = "abattery_battery_cycle_track"
        private const val KEY_CC_LAST = "charge_counter_last"
        private const val KEY_CC_ACC = "charge_counter_cycle_acc"
        private const val KEY_CC_EST = "charge_counter_est_cycles"
        const val HEALTH_EXCELLENT = 9
        const val HEALTH_FAIR = 8
    }

    suspend fun getBatteryInfo(): BatteryInfo = withContext(Dispatchers.IO) {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val pct = if (scale > 0) (level * 100 / scale) else 0

        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        val health = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val healthState = mapHealthState(health)

        val tempRaw = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val temp = tempRaw / 10f

        val voltageRaw = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        val voltage = voltageRaw / 1000f

        val technology = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        val plugTypes = pluggedTypes(plugged)
        val chargeStatus = batteryChargeStatus(status)

        // Not all SDK stubs expose these constants; keys match AOSP BatteryManager.
        val maxCurUa = intent?.run {
            getIntExtra("max_charging_current", -1).takeIf { it > 0 }
        }
        val maxVoltUv = intent?.run {
            getIntExtra("max_charging_voltage", -1).takeIf { it > 0 }
        }

        val bm = context.getSystemService(BatteryManager::class.java)
        val currentMicroA = readCurrentMicroA(bm)

        val chargePowerW = if (voltage > 0f && currentMicroA != null) {
            val amps = currentMicroA / 1_000_000.0
            abs(amps * voltage).toFloat()
        } else {
            null
        }

        val (dumpsys, dumpsysRaw) = readBatteryDumpsys()
        val levelFraction = if (scale > 0) level.toFloat() / scale else 1f

        val designMah = readDesignCapacityMah(dumpsys, dumpsysRaw)
        val fullMah = readFullChargeCapacityMah(dumpsys, levelFraction, bm, designMah)

        val (cycleCount, cycleEstimated) = resolveCycleCount(
            intent = intent,
            dumpsys = dumpsys,
            dumpsysRaw = dumpsysRaw,
            capacityMah = fullMah ?: designMah,
            batteryManager = bm,
        )

        BatteryInfo(
            percent = pct,
            chargeStatus = chargeStatus,
            plugTypes = plugTypes,
            isCharging = charging,
            healthState = healthState,
            temperatureCelsius = temp,
            voltageV = voltage,
            currentMicroA = currentMicroA,
            chargePowerW = chargePowerW,
            maxChargingCurrentUa = maxCurUa,
            maxChargingVoltageUv = maxVoltUv,
            designCapacityMah = designMah,
            fullChargeCapacityMah = fullMah,
            cycleCount = cycleCount,
            cycleCountEstimated = cycleEstimated,
            technology = technology,
        )
    }

    private fun batteryChargeStatus(status: Int): BatteryChargeStatus = when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> BatteryChargeStatus.CHARGING
        BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryChargeStatus.DISCHARGING
        BatteryManager.BATTERY_STATUS_FULL -> BatteryChargeStatus.FULL
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryChargeStatus.NOT_CHARGING
        else -> BatteryChargeStatus.UNKNOWN
    }

    private fun pluggedTypes(plugged: Int): List<PowerPlugType> {
        if (plugged == 0) return emptyList()
        val parts = mutableListOf<PowerPlugType>()
        if (plugged and BatteryManager.BATTERY_PLUGGED_AC != 0) parts.add(PowerPlugType.AC)
        if (plugged and BatteryManager.BATTERY_PLUGGED_USB != 0) parts.add(PowerPlugType.USB)
        if (plugged and BatteryManager.BATTERY_PLUGGED_DOCK != 0) parts.add(PowerPlugType.DOCK)
        if (plugged and BatteryManager.BATTERY_PLUGGED_WIRELESS != 0) parts.add(PowerPlugType.WIRELESS)
        return parts
    }

    private fun readCurrentMicroA(bm: BatteryManager?): Int? {
        if (bm == null) return null
        return try {
            val v = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            if (v == Int.MIN_VALUE || v == 0) null else v
        } catch (_: Exception) {
            null
        }
    }

    private fun readDesignCapacityMah(
        dumpsys: Map<String, String>,
        dumpsysRaw: String,
    ): Int? {
        val explicitPaths = listOf(
            "/sys/class/power_supply/battery/charge_full_design",
            "/sys/class/power_supply/Battery/charge_full_design",
            "/sys/class/power_supply/battery/charge_full_design_uah",
            "/sys/class/power_supply/Battery/charge_full_design_uah",
            "/sys/class/power_supply/bms/charge_full_design",
            "/sys/class/power_supply/bms/charge_full_design_uah",
            "/sys/class/power_supply/BMS/charge_full_design",
            "/sys/class/power_supply/google-battery/charge_full_design",
            "/sys/class/power_supply/maxfg/charge_full_design",
            "/sys/class/power_supply/maxfg/fg_fullcapnom",
            "/sys/class/power_supply/max170xx_battery/charge_full_design",
            "/sys/class/power_supply/max77705_battery/charge_full_design",
            "/sys/class/power_supply/qpnp-bms/charge_full_design",
        )
        readFirstMah(explicitPaths)?.let { return it }

        scanPowerSupplyDirsForDesign()?.let { return it }

        for ((key, value) in dumpsys) {
            val k = key.lowercase()
            if (!k.contains("design")) continue
            if (k.contains("cycle") || k.contains("health")) continue
            if (k.contains("charge") || k.contains("full") || k.contains("cap") || k.contains("fcc")) {
                parseMahFromLooseString(value)?.let { return it }
            }
        }

        parseDesignMahFromDumpsysRaw(dumpsysRaw)?.let { return it }

        readDesignCapacityFromPowerProfile()?.let { return it }

        return null
    }

    /** Nominal capacity from device power profile (OEM XML); works when sysfs is inaccessible. */
    private fun readDesignCapacityFromPowerProfile(): Int? {
        return try {
            val cls = Class.forName("com.android.internal.os.PowerProfile")
            val ctor = cls.getConstructor(Context::class.java)
            val inst = ctor.newInstance(context)
            val cap = cls.getMethod("getBatteryCapacity").invoke(inst) as? Double ?: return null
            val mah = cap.roundToInt()
            if (mah in 500..30_000) mah else null
        } catch (_: Throwable) {
            null
        }
    }

    /** Walk every power_supply node (OEM paths differ). */
    private fun scanPowerSupplyDirsForDesign(): Int? {
        val base = File("/sys/class/power_supply")
        if (!base.isDirectory) return null
        val children = base.listFiles() ?: return null
        val tryNames = listOf(
            "charge_full_design",
            "charge_full_design_uah",
            "full_design_capacity",
            "nominal_full_capacity",
            "design_capacity",
        )
        for (dir in children) {
            if (!dir.isDirectory) continue
            for (name in tryNames) {
                val f = File(dir, name)
                if (f.exists() && f.canRead()) {
                    readFirstMah(listOf(f.absolutePath))?.let { return it }
                }
            }
        }
        return null
    }

    private fun parseMahFromLooseString(raw: String): Int? {
        val s = raw.trim()
        val num = Regex("(\\d{3,})").find(s)?.groupValues?.get(1)?.toLongOrNull() ?: return null
        return when {
            num > 100_000L -> (num / 1000L).toInt()
            num in 500..30_000 -> num.toInt()
            else -> null
        }
    }

    private fun parseDesignMahFromDumpsysRaw(raw: String): Int? {
        if (raw.isBlank()) return null
        val patterns = listOf(
            Regex("""(?i)charge\s*full\s*design[^0-9]*(\d{3,})"""),
            Regex("""(?i)full\s*design[^0-9]*(\d{3,})"""),
            Regex("""(?i)design\s*capacity[^0-9]*(\d{3,})"""),
            Regex("""(?i)nominal[^0-9]*(\d{3,})\s*mAh"""),
            Regex("""(?i)rated[^0-9]*(\d{3,})\s*mAh"""),
            Regex("""(?i)mChargeFullDesign[^0-9]*(\d{3,})"""),
        )
        for (re in patterns) {
            val m = re.find(raw) ?: continue
            val n = m.groupValues.getOrNull(1)?.toLongOrNull() ?: continue
            val mah = when {
                n > 100_000L -> (n / 1000L).toInt()
                n in 500..30_000 -> n.toInt()
                else -> continue
            }
            return mah
        }
        return null
    }

    private fun readFullChargeCapacityMah(
        dumpsys: Map<String, String>,
        levelFraction: Float,
        bm: BatteryManager?,
        designFallback: Int?,
    ): Int? {
        val paths = listOf(
            "/sys/class/power_supply/battery/charge_full",
            "/sys/class/power_supply/Battery/charge_full",
            "/sys/class/power_supply/bms/charge_full",
        )
        readFirstMah(paths)?.let { return it }

        val chargeCounter = dumpsys.entries
            .firstOrNull { it.key.contains("charge counter", ignoreCase = true) }
            ?.value?.trim()?.toLongOrNull()
        if (chargeCounter != null && chargeCounter > 0 && levelFraction > 0f) {
            val fullUAh = chargeCounter / levelFraction
            val mah = (fullUAh / 1000f).toInt()
            if (mah in 500..30_000) return mah
        }
        try {
            if (bm != null) {
                val counter = readChargeCounterMicroAh(bm)
                if (counter > 0L && levelFraction > 0f) {
                    val fullUAh = counter / levelFraction
                    val mah = (fullUAh / 1000f).toInt()
                    if (mah in 500..30_000) return mah
                }
            }
        } catch (_: Exception) {
        }
        readCapacityMahFromBatteryManager()?.let { return it }
        return designFallback
    }

    private fun readFirstMah(paths: List<String>): Int? {
        for (path in paths) {
            try {
                val raw = File(path).readText().trim().toLongOrNull() ?: continue
                val mah = if (raw > 100_000L) (raw / 1000L).toInt() else raw.toInt()
                if (mah in 500..30_000) return mah
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun readBatteryDumpsys(): Pair<Map<String, String>, String> {
        return try {
            val process = Runtime.getRuntime().exec("dumpsys battery")
            val output = try {
                BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            } finally {
                process.destroy()
            }
            val map = output.lines()
                .mapNotNull { line ->
                    val parts = line.trim().split(":", limit = 2)
                    if (parts.size == 2) parts[0].trim() to parts[1].trim()
                    else null
                }
                .toMap()
            map to output
        } catch (_: Exception) {
            emptyMap<String, String>() to ""
        }
    }

    private fun parseLeadingInt(value: String): Int? =
        Regex("\\d+").find(value)?.value?.toIntOrNull()

    private fun readCycleCountFromBroadcast(intent: Intent?): Int? {
        val extras = intent?.extras ?: return null
        if (!extras.containsKey(BatteryManager.EXTRA_CYCLE_COUNT)) return null
        val v = extras.getInt(BatteryManager.EXTRA_CYCLE_COUNT, -1)
        return v.takeIf { it >= 0 }
    }

    private fun parseCycleFromDumpsysRaw(raw: String): Int? {
        if (raw.isBlank()) return null
        val patterns = listOf(
            Regex("""(?i)cycle\s*count[^:\n]{0,24}:\s*(\d+)"""),
            Regex("""(?i)mCycleCount\s*=\s*(\d+)"""),
            Regex("""(?i)charge\s*cycles?\s*:\s*(\d+)"""),
            Regex("""(?i)batt_cycle_count\s*:\s*(\d+)"""),
        )
        for (re in patterns) {
            val n = re.find(raw)?.groupValues?.getOrNull(1)?.toIntOrNull()
            if (n != null && n > 0) return n
        }
        return null
    }

    private fun readCycleCountSysfs(): Int? {
        val sysfsPaths = listOf(
            "/sys/class/power_supply/battery/battery_cycle_count",
            "/sys/class/power_supply/battery/battery_cycle",
            "/sys/class/power_supply/battery/cycle_count",
            "/sys/class/power_supply/battery/cycle",
            "/sys/class/power_supply/Battery/cycle_count",
            "/sys/class/power_supply/bms/cycle_count",
            "/sys/class/power_supply/bms/charge_cycles",
            "/sys/class/power_supply/BMS/cycle_count",
            "/sys/class/power_supply/max170xx_battery/cycle_count",
            "/sys/class/power_supply/max170xx/cycle_count",
            "/sys/class/power_supply/maxfg/cycle_count",
            "/sys/class/power_supply/max77705_battery/cycle_count",
            "/sys/class/power_supply/qpnp-bms/cycle_count",
            "/sys/class/power_supply/google-battery/cycle_count",
            "/sys/class/power_supply/smb1351-battery/cycle_count",
            "/sys/class/power_supply/smb138x-battery/cycle_count",
            "/sys/class/power_supply/battery/charge_cycles",
        )
        for (path in sysfsPaths) {
            try {
                val file = File(path)
                if (file.exists() && file.canRead()) {
                    val text = file.readText().trim()
                    val v = text.toIntOrNull()
                    if (v != null && v > 0) return v
                }
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun estimateCyclesFromChargeCounter(
        bm: BatteryManager?,
        capacityMah: Int?,
    ): Int? {
        if (bm == null || capacityMah == null || capacityMah !in 500..30_000) return null
        val counter = readChargeCounterMicroAh(bm)
        if (counter <= 0L) return null

        val designMicroAh = capacityMah.toLong() * 1_000_000L
        if (designMicroAh <= 0L) return null

        val prefs = context.getSharedPreferences(PREFS_CYCLE_TRACK, Context.MODE_PRIVATE)
        val last = prefs.getLong(KEY_CC_LAST, -1L)
        var acc = prefs.getFloat(KEY_CC_ACC, 0f)
        var est = prefs.getInt(KEY_CC_EST, 0)

        if (last > 0L && counter > last) {
            val delta = counter - last
            if (delta in 1 until designMicroAh * 3) {
                acc += delta.toFloat() / designMicroAh.toFloat()
            }
        }

        while (acc >= 0.999f) {
            est++
            acc -= 1f
        }

        prefs.edit()
            .putLong(KEY_CC_LAST, counter)
            .putFloat(KEY_CC_ACC, acc)
            .putInt(KEY_CC_EST, est)
            .apply()

        return est.takeIf { it > 0 }
    }

    private fun resolveCycleCount(
        intent: Intent?,
        dumpsys: Map<String, String>,
        dumpsysRaw: String,
        capacityMah: Int?,
        batteryManager: BatteryManager?,
    ): Pair<Int?, Boolean> {
        readCycleCountFromBroadcast(intent)?.let { return it to false }

        val fromDumpsysMap = dumpsys.entries
            .firstOrNull { it.key.contains("cycle", ignoreCase = true) }
            ?.value
            ?.let { parseLeadingInt(it) }
            ?.takeIf { it > 0 }
        if (fromDumpsysMap != null) return fromDumpsysMap to false

        parseCycleFromDumpsysRaw(dumpsysRaw)?.let { return it to false }

        readCycleCountSysfs()?.let { return it to false }

        val est = estimateCyclesFromChargeCounter(batteryManager, capacityMah)
        if (est != null) return est to true

        return null to false
    }

    private fun readChargeCounterMicroAh(bm: BatteryManager): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val v = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            if (v != Long.MIN_VALUE && v > 0) v else -1L
        } else {
            val v = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            if (v > 0) v.toLong() else -1L
        }
    }

    private fun mapHealthState(health: Int): BatteryHealthState {
        if (health == -1) return BatteryHealthState.UNKNOWN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM && health == HEALTH_EXCELLENT) {
            return BatteryHealthState.EXCELLENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && health == HEALTH_FAIR) {
            return BatteryHealthState.FAIR
        }
        return when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealthState.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT,
            BatteryManager.BATTERY_HEALTH_DEAD,
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE,
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE,
            BatteryManager.BATTERY_HEALTH_COLD,
            -> BatteryHealthState.POOR
            else -> BatteryHealthState.UNKNOWN
        }
    }

    private fun readCapacityMahFromBatteryManager(): Int? {
        return try {
            val b = context.getSystemService(BatteryManager::class.java) ?: return null
            val cap = b.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            if (cap <= 0) return null
            val mah = when {
                cap > 1_000_000 -> cap / 1_000_000
                cap > 1_000 -> cap / 1000
                else -> return null
            }
            if (mah in 500..30_000) mah else null
        } catch (_: Exception) {
            null
        }
    }
}
