package com.abanana.abattery.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.abanana.abattery.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val Manrope = FontFamily(Font(GoogleFont("Manrope"), provider, FontWeight.Bold))
val SpaceGrotesk = FontFamily(Font(GoogleFont("Space Grotesk"), provider, FontWeight.Normal))
val InterFont = FontFamily(Font(GoogleFont("Inter"), provider, FontWeight.Normal))
