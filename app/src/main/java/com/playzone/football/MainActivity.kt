package com.playzone.football

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playzone.football.ui.navigation.MainScreen
import com.playzone.football.ui.theme.PlayZoneTheme
import com.playzone.football.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayZoneTheme {
                val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
