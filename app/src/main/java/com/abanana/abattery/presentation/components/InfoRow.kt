package com.abanana.abattery.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abanana.abattery.ui.theme.InterFont
import com.abanana.abattery.ui.theme.OnSurface
import com.abanana.abattery.ui.theme.OnSurfaceVar
import com.abanana.abattery.ui.theme.OutlineVariant
import com.abanana.abattery.ui.theme.SpaceGrotesk

@Composable
fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = OnSurface,
    valueMaxLines: Int = 1,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                label,
                modifier = Modifier.padding(end = 8.dp),
                color = OnSurfaceVar,
                fontSize = 14.sp,
                fontFamily = InterFont,
            )
            Text(
                value,
                modifier = Modifier.weight(1f, fill = false),
                color = valueColor,
                fontSize = 13.sp,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                maxLines = valueMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.1f), thickness = 0.5.dp)
    }
}
