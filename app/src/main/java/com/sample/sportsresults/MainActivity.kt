package com.sample.sportsresults

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.sample.sportsresults.presentation.DashboardScreen
import com.sample.sportsresults.presentation.SportsViewModel
import com.sample.sportsresults.ui.theme.SampleTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleTheme {
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