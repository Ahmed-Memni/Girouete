package com.example.girouette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.girouette.ui.theme.GirouetteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GirouetteTheme {
                // Now calling the new Preset-based UI instead of GirouetteScreen
                DisplayPresetScreen()
            }
        }
    }
}
