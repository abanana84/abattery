package com.abanana.abattery.ui.theme

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    ;

    companion object {
        fun fromOrdinal(ordinal: Int): ThemeMode =
            entries.getOrElse(ordinal) { SYSTEM }
    }
}
