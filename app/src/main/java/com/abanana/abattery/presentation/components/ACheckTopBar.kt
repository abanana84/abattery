package com.abanana.abattery.presentation.components

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.abanana.abattery.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abanana.abattery.ui.theme.BgDark
import com.abanana.abattery.ui.theme.Manrope
import com.abanana.abattery.ui.theme.OnSurface
import com.abanana.abattery.ui.theme.OutlineColor
import com.abanana.abattery.ui.theme.PrimaryGreen

@Composable
fun ACheckTopBar(
    onRefresh: () -> Unit,
) {
    var aboutOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
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

    if (aboutOpen) {
        AlertDialog(
            onDismissRequest = { aboutOpen = false },
            title = {
                Text(
                    stringResource(R.string.app_name),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                )
            },
            text = {
                Text(
                    "Version $versionName",
                    fontFamily = Manrope,
                    color = OnSurface,
                    fontSize = 14.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = { aboutOpen = false }) {
                    Text("OK", color = PrimaryGreen)
                }
            },
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 12.dp),
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
                tint = PrimaryGreen,
                modifier = Modifier.size(22.dp),
            )
            Text(
                barTitle,
                modifier = Modifier.weight(1f, fill = false),
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = PrimaryGreen,
                letterSpacing = (-0.3).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(
                onClick = onRefresh,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    tint = OutlineColor,
                    modifier = Modifier.size(22.dp),
                )
            }
            IconButton(
                onClick = { aboutOpen = true },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Options",
                    tint = OutlineColor,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
