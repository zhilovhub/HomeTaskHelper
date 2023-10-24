package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hometaskhelper.ui.theme.HomeTaskHelperTheme
import com.example.hometaskhelper.ui.viewmodels.MainViewModel
import com.example.hometaskhelper.ui.viewmodels.UserState


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(LocalContext.current.applicationContext))
) {
    val tasksState by viewModel.tasksState.collectAsState()
    val userState by viewModel.userState.collectAsState()

    val tasksRemembered = remember { mutableStateListOf(*tasksState.toTypedArray()) }
    if (tasksRemembered.size != tasksState.size) {
        tasksRemembered.removeRange(0, tasksRemembered.size)
        tasksRemembered.addAll(tasksState)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HomeTasks",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )
                Tasks(
                    userState = userState,
                    tasks = tasksRemembered,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
                RedactTasks(
                    userState = userState,
                    viewModel = viewModel
                )
            }
            if (userState != UserState.DEFAULT) {
                AcceptCancel(
                    viewModel = viewModel,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeTaskHelperTheme {
        HomeScreen()
    }
}
