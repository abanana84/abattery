package com.abanana.abattery.presentation.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.abanana.abattery.presentation.battery.BatteryScreen
import com.abanana.abattery.ui.theme.ABatteryTheme
import com.abanana.abattery.ui.theme.AppTheme
import com.abanana.abattery.ui.theme.ThemeMode
import com.abanana.abattery.ui.theme.rememberThemeDark
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        setContent {
            var themeMode by remember {
                mutableStateOf(ThemeMode.fromOrdinal(prefs.getInt(KEY_THEME_MODE, ThemeMode.SYSTEM.ordinal)))
            }
            val darkTheme = rememberThemeDark(themeMode)
            val activity = LocalContext.current as AppCompatActivity

            SideEffect {
                val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }

            val onThemeModeChange: (ThemeMode) -> Unit = { mode ->
                themeMode = mode
                prefs.edit().putInt(KEY_THEME_MODE, mode.ordinal).apply()
            }

            ABatteryTheme(darkTheme = darkTheme) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.background),
                    containerColor = AppTheme.colors.background,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(AppTheme.colors.background),
                    ) {
                        BatteryScreen(
                            themeMode = themeMode,
                            onThemeModeChange = onThemeModeChange,
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val PREFS_NAME = "abattery_prefs"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
