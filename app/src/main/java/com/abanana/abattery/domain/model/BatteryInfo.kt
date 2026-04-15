package com.abanana.abattery.domain.model

data class BatteryInfo(
    val percent: Int,
    /** Charging / Discharging / Full / … */
    val statusLabel: String,
    /** AC, USB, Wireless, or on battery. */
    val powerInputLabel: String,
    val isCharging: Boolean,
    val healthLabel: String,
    val temperatureCelsius: Float,
    val voltageV: Float,
    /** Instant current (µA); negative when discharging on most devices. */
    val currentMicroA: Int?,
    /** Estimated electrical power (W); magnitude when useful. */
    val chargePowerW: Float?,
    /** From [BatteryManager.EXTRA_MAX_CHARGING_CURRENT] (µA), if available. */
    val maxChargingCurrentUa: Int?,
    /** From [BatteryManager.EXTRA_MAX_CHARGING_VOLTAGE] (µV), if available. */
    val maxChargingVoltageUv: Int?,
    /** Nominal design capacity (mAh) from sysfs when exposed. */
    val designCapacityMah: Int?,
    /** Last full / learned full charge capacity (mAh) when exposed or inferred. */
    val fullChargeCapacityMah: Int?,
    val cycleCount: Int?,
    val cycleCountEstimated: Boolean = false,
    /** Raw technology string (Li-ion, Li-poly, etc.). */
    val technology: String,
)
