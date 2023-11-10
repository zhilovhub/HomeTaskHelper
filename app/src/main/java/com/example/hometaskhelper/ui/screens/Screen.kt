package com.example.hometaskhelper.ui.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hometaskhelper.ui.viewmodels.MainViewModel

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(LocalContext.current.applicationContext))
) {
    val hasAuthorized = viewModel.hasAuthorized.collectAsState()

    if (hasAuthorized.value) {
        HomeScreen(
            modifier = modifier,
            viewModel = viewModel
        )
    } else {
        AuthScreen(
            modifier = modifier,
//            viewModel = viewModel
        )
    }
}