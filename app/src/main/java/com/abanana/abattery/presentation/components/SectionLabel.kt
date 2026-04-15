package com.abanana.abattery.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.abanana.abattery.ui.theme.AppTheme
import com.abanana.abattery.ui.theme.SpaceGrotesk

// Uppercase tiny tracking label — e.g. "PROCESSOR UNIT", "ACTIVE POLLING"
@Composable
fun SectionLabel(text: String, color: Color? = null) {
    Text(
        text = text.uppercase(),
        fontFamily = SpaceGrotesk,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = color ?: AppTheme.colors.outline,
        letterSpacing = 1.5.sp,
    )
}
