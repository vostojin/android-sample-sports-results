package com.mozzart.th.vostojin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.mozzart.th.vostojin.presentation.DashboardScreen
import com.mozzart.th.vostojin.presentation.SportsViewModel
import com.mozzart.th.vostojin.ui.theme.MozzartTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MozzartTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val viewModel: SportsViewModel = koinViewModel()

    DashboardScreen(viewModel)
}