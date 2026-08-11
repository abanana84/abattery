package com.abanana.abattery.presentation.locale

import java.util.Locale

/**
 * BCP-47 tags matching `values-*` resource folders (Indonesian resources use `values-id`).
 */
object SupportedAppLocales {
    val tagsInOrder: List<String> = listOf(
        "en",
        "de",
        "es",
        "fr",
        "id",
        "it",
        "ja",
        "ko",
        "nl",
        "pl",
        "pt",
        "ru",
        "tr",
        "uk",
        "vi",
        "zh-CN",
    )

    fun nativeDisplayName(tag: String): String {
        val loc = Locale.forLanguageTag(tag)
        return loc.getDisplayName(loc).replaceFirstChar { ch ->
            if (ch.isLowerCase()) ch.titlecase(loc) else ch.toString()
        }
    }
}
