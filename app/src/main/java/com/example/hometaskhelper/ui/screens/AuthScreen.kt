package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hometaskhelper.ui.viewmodels.MainViewModel


@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
//    viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(LocalContext.current.applicationContext))
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

        }
    }
}


@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}