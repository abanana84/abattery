package com.abanana.abattery.presentation.battery

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abanana.abattery.R
import com.abanana.abattery.domain.model.BatteryInfo
import com.abanana.abattery.presentation.components.ACheckProgressBar
import com.abanana.abattery.presentation.components.ACheckTopBar
import com.abanana.abattery.presentation.components.InfoRow
import com.abanana.abattery.presentation.components.SectionCard
import com.abanana.abattery.presentation.components.SectionLabel
import com.abanana.abattery.ui.theme.BgDark
import com.abanana.abattery.ui.theme.InterFont
import com.abanana.abattery.ui.theme.Manrope
import com.abanana.abattery.ui.theme.OnSurface
import com.abanana.abattery.ui.theme.OnSurfaceVar
import com.abanana.abattery.ui.theme.OutlineColor
import com.abanana.abattery.ui.theme.OutlineVariant
import com.abanana.abattery.ui.theme.PrimaryGreen
import com.abanana.abattery.ui.theme.SecondaryColor
import com.abanana.abattery.ui.theme.SpaceGrotesk
import com.abanana.abattery.ui.theme.SurfaceHigh
import com.abanana.abattery.ui.theme.SurfaceHighest
import com.abanana.abattery.ui.theme.SurfaceLow
import com.abanana.abattery.util.formatBatteryCycleCount

@Composable
fun BatteryScreen(viewModel: BatteryViewModel = hiltViewModel()) {
    val info by viewModel.batteryInfo.collectAsStateWithLifecycle()
    val currentHistory by viewModel.currentHistoryMa.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(BgDark)) {
        ACheckTopBar(onRefresh = { viewModel.refreshNow() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal),
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(4.dp))

            SectionCard(color = SurfaceHigh) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        SectionLabel("Status")
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = info?.statusLabel ?: "—",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            color = OnSurface,
                        )
                        Text(
                            text = info?.powerInputLabel ?: "",
                            fontFamily = SpaceGrotesk,
                            fontSize = 12.sp,
                            color = PrimaryGreen,
                        )
                    }
                    BatteryCircularGauge(
                        percent = info?.percent ?: 0,
                        isCharging = info?.isCharging ?: false,
                        modifier = Modifier.size(120.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SectionCard(modifier = Modifier.weight(1f)) {
                    SectionLabel("Temperature")
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "%.1f".format(info?.temperatureCelsius ?: 0f),
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = OnSurface,
                        )
                        Text(
                            "°C",
                            fontFamily = SpaceGrotesk,
                            fontSize = 14.sp,
                            color = OutlineColor,
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    ACheckProgressBar(
                        progress = ((info?.temperatureCelsius ?: 0f) / 60f).coerceIn(0f, 1f),
                        color = SecondaryColor,
                    )
                }
                SectionCard(modifier = Modifier.weight(1f)) {
                    SectionLabel("Voltage")
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "%.3f".format(info?.voltageV ?: 0f),
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = OnSurface,
                        )
                        Text(
                            "V",
                            fontFamily = SpaceGrotesk,
                            fontSize = 14.sp,
                            color = OutlineColor,
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                }
            }

            SectionCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionLabel("Power")
                    Text(
                        text = formatPowerW(info?.chargePowerW),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = PrimaryGreen,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "|V × I|",
                    fontFamily = SpaceGrotesk,
                    fontSize = 10.sp,
                    color = OutlineColor,
                )
            }

            SectionCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.current_flow_title),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OnSurface,
                    )
                    Text(
                        text = formatCurrentMa(info?.currentMicroA),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = OnSurface,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.current_flow_subtitle),
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    color = OutlineColor,
                )
                Spacer(Modifier.height(12.dp))
                BatteryCurrentFlowChart(
                    samples = currentHistory,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SectionCard(color = SurfaceHigh) {
                Text(
                    "Battery chemistry & health",
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = OnSurface,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = OutlineVariant.copy(alpha = 0.15f),
                )
                InfoRow("Technology", formatTechnology(info?.technology))
                InfoRow(
                    label = "Health",
                    value = info?.healthLabel ?: "N/A",
                    valueColor = PrimaryGreen,
                )
            }

            SectionCard {
                SectionLabel("Charge capability (reported)")
                Spacer(Modifier.height(8.dp))
                InfoRow("Max charging current", formatMaxUa(info?.maxChargingCurrentUa))
                val curUa = info?.maxChargingCurrentUa
                if (curUa != null && curUa > 0) {
                    Spacer(Modifier.height(6.dp))
                    ACheckProgressBar(
                        progress = (curUa / 3_000_000f).coerceIn(0f, 1f),
                        color = PrimaryGreen,
                    )
                }
                Spacer(Modifier.height(10.dp))
                InfoRow("Max charging voltage", formatMaxUv(info?.maxChargingVoltageUv))
                val maxUv = info?.maxChargingVoltageUv
                if (maxUv != null && maxUv > 0) {
                    Spacer(Modifier.height(6.dp))
                    ACheckProgressBar(
                        progress = (maxUv / 12_000_000f).coerceIn(0f, 1f),
                        color = SecondaryColor,
                    )
                }
            }

            SectionCard {
                SectionLabel("Capacity & cycles")
                Spacer(Modifier.height(8.dp))
                InfoRow("Design capacity", formatMah(info?.designCapacityMah))
                InfoRow("Full / estimated capacity", formatMah(info?.fullChargeCapacityMah))
                InfoRow(
                    "Cycle count",
                    formatBatteryCycleCount(
                        info?.cycleCount,
                        info?.cycleCountEstimated == true,
                    ),
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceLow,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp),
                    )
                    Text(
                        text = buildBatteryMessage(info),
                        fontFamily = InterFont,
                        fontSize = 13.sp,
                        color = OnSurfaceVar,
                        lineHeight = 18.sp,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun formatTechnology(raw: String?): String {
    if (raw.isNullOrBlank() || raw.equals("Unknown", true)) return "N/A"
    return raw.trim()
}

private fun formatCurrentMa(microA: Int?): String {
    if (microA == null) return "N/A"
    val ma = microA / 1000f
    return "%+.0f mA".format(ma)
}

private fun formatPowerW(w: Float?): String {
    if (w == null || w <= 0f) return "N/A"
    return "%.2f W".format(w)
}

private fun formatMah(mah: Int?): String =
    if (mah != null && mah > 0) "$mah mAh" else "N/A"

private fun formatMaxUa(ua: Int?): String =
    when {
        ua == null || ua <= 0 -> "N/A"
        else -> "%d mA".format(ua / 1000)
    }

private fun formatMaxUv(uv: Int?): String =
    when {
        uv == null || uv <= 0 -> "N/A"
        else -> "%.2f V".format(uv / 1_000_000.0)
    }

fun buildBatteryMessage(info: BatteryInfo?): String {
    if (info == null) return "Reading battery information..."
    return when (info.healthLabel) {
        "Excellent", "Good" ->
            "Your battery is functioning optimally. Use an original or certified adapter when possible."
        "Fair" ->
            "Battery health is fair. Reducing heat and very fast charging sessions can help longevity."
        "Poor" ->
            "Battery health is poor. Consider service or replacement if runtime is unacceptable."
        else ->
            "Status: ${info.statusLabel}. Health: ${info.healthLabel}."
    }
}

@Composable
fun BatteryCurrentFlowChart(samples: List<Float>, modifier: Modifier = Modifier) {
    val line = PrimaryGreen
    val gridColor = OutlineVariant.copy(alpha = 0.22f)
    val chartBg = SurfaceHighest.copy(alpha = 0.35f)
    Box(
        modifier
            .fillMaxWidth()
            .height(168.dp)
            .background(chartBg, RoundedCornerShape(12.dp)),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val pad = 12.dp.toPx()
            val w = (size.width - pad * 2).coerceAtLeast(1f)
            val h = (size.height - pad * 2).coerceAtLeast(1f)
            val gridLines = 5
            for (i in 0..gridLines) {
                val gy = pad + i * h / gridLines
                drawLine(
                    color = gridColor,
                    start = Offset(pad, gy),
                    end = Offset(size.width - pad, gy),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            if (samples.size < 2) return@Canvas
            val minV = samples.minOrNull() ?: 0f
            val maxV = samples.maxOrNull() ?: 1f
            val spanRaw = (maxV - minV).coerceAtLeast(1f)
            val inset = spanRaw * 0.08f
            val minAdj = minV - inset
            val maxAdj = maxV + inset
            val span = (maxAdj - minAdj).coerceAtLeast(1f)
            val path = Path()
            samples.forEachIndexed { i, v ->
                val t = i / (samples.size - 1).coerceAtLeast(1).toFloat()
                val x = pad + t * w
                val y = pad + h - ((v - minAdj) / span) * h
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path,
                color = line,
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round),
            )
        }
        if (samples.size < 2) {
            Text(
                text = "…",
                modifier = Modifier.align(Alignment.Center),
                fontFamily = SpaceGrotesk,
                fontSize = 22.sp,
                color = OutlineColor.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
fun BatteryCircularGauge(percent: Int, isCharging: Boolean, modifier: Modifier = Modifier) {
    val animPct by animateFloatAsState(
        targetValue = percent.toFloat(),
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "battery_gauge",
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            val sweepAngle = animPct / 100f * 360f
            drawArc(
                color = SurfaceHighest,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = PrimaryGreen,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$percent",
                fontFamily = Manrope,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                color = OnSurface,
            )
            Text(
                "%",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = OnSurface.copy(alpha = 0.5f),
            )
            Spacer(Modifier.height(2.dp))
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = PrimaryGreen.copy(alpha = 0.1f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = if (isCharging) Icons.Filled.Bolt else Icons.Filled.BatteryFull,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        if (isCharging) "Charging" else "On battery",
                        fontFamily = SpaceGrotesk,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                    )
                }
            }
        }
    }
}
