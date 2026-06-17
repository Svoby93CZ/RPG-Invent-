package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainAppContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RPGViewModel

class MainActivity : ComponentActivity() {
    
    // Initialize our core RPG ViewModel using standard constructor delegation
    private val viewModel: RPGViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}
