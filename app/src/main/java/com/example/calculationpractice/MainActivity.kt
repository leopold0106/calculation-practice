package com.example.calculationpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculationpractice.ui.GameScreen
import com.example.calculationpractice.ui.theme.CalculationPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculationPracticeTheme {
                val gameViewModel: GameViewModel = viewModel()
                var uiState by remember { mutableStateOf(gameViewModel.uiState) }

                GameScreen(
                    uiState = uiState,
                    onInputChanged = { input ->
                        gameViewModel.onInputChanged(input)
                        uiState = gameViewModel.uiState
                    },
                    onSubmit = {
                        gameViewModel.onSubmit()
                        uiState = gameViewModel.uiState
                    }
                )
            }
        }
    }
}
