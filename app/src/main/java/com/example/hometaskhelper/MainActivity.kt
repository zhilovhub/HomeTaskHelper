package com.example.hometaskhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.hometaskhelper.ui.screens.Screen
import com.example.hometaskhelper.ui.theme.HomeTaskHelperTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTaskHelperTheme {
                Screen()
            }
        }
    }
}
