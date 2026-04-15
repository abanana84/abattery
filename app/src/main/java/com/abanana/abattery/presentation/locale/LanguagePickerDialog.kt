package com.abanana.abattery.presentation.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.abanana.abattery.R
import java.util.Locale
import com.abanana.abattery.ui.theme.AppTheme
import com.abanana.abattery.ui.theme.Manrope

@Composable
fun LanguagePickerDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    val c = AppTheme.colors
    val scroll = rememberScrollState()
    val rawTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val followingSystem = rawTags.isBlank()
    val selectedTag = if (rawTags.isBlank()) {
        null
    } else {
        rawTags.split(",").firstOrNull()?.trim()?.takeIf { it.isNotEmpty() }
    }

    fun matchesSavedTag(tag: String): Boolean {
        if (followingSystem || selectedTag == null) return false
        val primary = selectedTag
        if (primary.equals(tag, ignoreCase = true)) return true
        val lp = Locale.forLanguageTag(primary)
        val lt = Locale.forLanguageTag(tag)
        return lp.language.equals(lt.language, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.language_title),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = Manrope,
                color = c.onSurface,
            )
        },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .verticalScroll(scroll)
                    .selectableGroup(),
            ) {
                val systemSelected = followingSystem
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = systemSelected,
                            onClick = {
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.getEmptyLocaleList(),
                                )
                                onDismiss()
                            },
                            role = Role.RadioButton,
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = systemSelected,
                        onClick = {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.getEmptyLocaleList(),
                            )
                            onDismiss()
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = c.primaryGreen),
                    )
                    Text(
                        text = stringResource(R.string.language_follow_system),
                        color = c.onSurface,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                Spacer(Modifier.height(4.dp))
                SupportedAppLocales.tagsInOrder.forEach { tag ->
                    val selected = !followingSystem && matchesSavedTag(tag)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selected,
                                onClick = {
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags(tag),
                                    )
                                    onDismiss()
                                },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = {
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(tag),
                                )
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = c.primaryGreen),
                        )
                        Text(
                            text = SupportedAppLocales.nativeDisplayName(tag),
                            color = c.onSurface,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close), color = c.primaryGreen)
            }
        },
    )
}
