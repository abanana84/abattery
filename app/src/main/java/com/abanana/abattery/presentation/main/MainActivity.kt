package com.abanana.abattery.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.abanana.abattery.presentation.battery.BatteryScreen
import com.abanana.abattery.ui.theme.ABatteryTheme
import com.abanana.abattery.ui.theme.BgDark
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ABatteryTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgDark),
                    containerColor = BgDark,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(BgDark),
                    ) {
                        BatteryScreen()
                    }
                }
            }
        }
    }
}
