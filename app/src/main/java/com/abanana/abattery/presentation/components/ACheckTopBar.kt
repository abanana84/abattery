package com.abanana.abattery.presentation.components

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abanana.abattery.R
import com.abanana.abattery.presentation.locale.LanguagePickerDialog
import com.abanana.abattery.ui.theme.AppTheme
import com.abanana.abattery.ui.theme.Manrope
import com.abanana.abattery.ui.theme.ThemeMode

@Composable
fun ACheckTopBar(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var themeMenuExpanded by remember { mutableStateOf(false) }
    var aboutOpen by remember { mutableStateOf(false) }
    var languageOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val c = AppTheme.colors
    val barTitle = stringResource(R.string.app_bar_title)
    val versionName = remember {
        try {
            val pm = context.packageManager
            val pkg = context.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(pkg, 0).versionName
            }
        } catch (_: Exception) {
            "—"
        }
    }

    LanguagePickerDialog(visible = languageOpen, onDismiss = { languageOpen = false })

    if (aboutOpen) {
        AlertDialog(
            onDismissRequest = { aboutOpen = false },
            title = {
                Text(
                    stringResource(R.string.app_name),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    color = c.primaryGreen,
                )
            },
            text = {
                Text(
                    stringResource(R.string.dialog_version, versionName ?: "—"),
                    fontFamily = Manrope,
                    color = c.onSurface,
                    fontSize = 14.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = { aboutOpen = false }) {
                    Text(stringResource(R.string.action_ok), color = c.primaryGreen)
                }
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.background)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Filled.BatteryChargingFull,
                    contentDescription = null,
                    tint = c.primaryGreen,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    barTitle,
                    modifier = Modifier.weight(1f, fill = false),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = c.primaryGreen,
                    letterSpacing = (-0.3).sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Box {
                    IconButton(
                        onClick = { themeMenuExpanded = true },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = themeModeIcon(themeMode),
                            contentDescription = stringResource(R.string.cd_theme_menu),
                            tint = c.outline,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    DropdownMenu(
                        expanded = themeMenuExpanded,
                        onDismissRequest = { themeMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.theme_system),
                                    fontFamily = Manrope,
                                )
                            },
                            onClick = {
                                themeMenuExpanded = false
                                onThemeModeChange(ThemeMode.SYSTEM)
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.theme_light),
                                    fontFamily = Manrope,
                                )
                            },
                            onClick = {
                                themeMenuExpanded = false
                                onThemeModeChange(ThemeMode.LIGHT)
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.theme_dark),
                                    fontFamily = Manrope,
                                )
                            },
                            onClick = {
                                themeMenuExpanded = false
                                onThemeModeChange(ThemeMode.DARK)
                            },
                        )
                    }
                }
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.cd_more_options),
                            tint = c.outline,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.menu_language),
                                    fontFamily = Manrope,
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                languageOpen = true
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.menu_about),
                                    fontFamily = Manrope,
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                aboutOpen = true
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun themeModeIcon(mode: ThemeMode): ImageVector = when (mode) {
    ThemeMode.SYSTEM -> Icons.Filled.BrightnessAuto
    ThemeMode.LIGHT -> Icons.Filled.LightMode
    ThemeMode.DARK -> Icons.Filled.DarkMode
}
