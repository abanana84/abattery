package com.abanana.abattery.presentation.locale

import androidx.annotation.StringRes
import com.abanana.abattery.R
import com.abanana.abattery.domain.model.BatteryChargeStatus
import com.abanana.abattery.domain.model.BatteryHealthState
import com.abanana.abattery.domain.model.PowerPlugType

@StringRes
fun BatteryChargeStatus.labelRes(): Int = when (this) {
    BatteryChargeStatus.CHARGING -> R.string.status_charging
    BatteryChargeStatus.DISCHARGING -> R.string.status_discharging
    BatteryChargeStatus.FULL -> R.string.status_full
    BatteryChargeStatus.NOT_CHARGING -> R.string.status_not_charging
    BatteryChargeStatus.UNKNOWN -> R.string.status_unknown
}

@StringRes
fun BatteryHealthState.labelRes(): Int = when (this) {
    BatteryHealthState.EXCELLENT -> R.string.health_excellent
    BatteryHealthState.GOOD -> R.string.health_good
    BatteryHealthState.FAIR -> R.string.health_fair
    BatteryHealthState.POOR -> R.string.health_poor
    BatteryHealthState.UNKNOWN -> R.string.health_unknown
}

@StringRes
fun PowerPlugType.labelRes(): Int = when (this) {
    PowerPlugType.AC -> R.string.plug_ac
    PowerPlugType.USB -> R.string.plug_usb
    PowerPlugType.DOCK -> R.string.plug_dock
    PowerPlugType.WIRELESS -> R.string.plug_wireless
}
