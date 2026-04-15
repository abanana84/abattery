package com.abanana.abattery.domain.model

enum class BatteryChargeStatus {
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING,
    UNKNOWN,
}

enum class PowerPlugType {
    AC,
    USB,
    DOCK,
    WIRELESS,
}

enum class BatteryHealthState {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    UNKNOWN,
}
