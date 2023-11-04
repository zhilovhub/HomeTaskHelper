package com.example.hometaskhelper.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.hometaskhelper.ui.models.ModelTask
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
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_START) {
                viewModel.resetIsRedactingTasks()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Log.d("HomeScreen", "TASKS:")
    for (task in tasksState.tasks.toList()) {
        Log.d("HomeScreen", task.toString())
    }
    Log.d("HomeScreen", "${userState.name}\n\n")

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
                    tasks = tasksState.tasks,
                    subjects = tasksState.subjects,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
                RedactTasks(
                    userState = userState,
                    onUpdateUserState = {
                        viewModel.updateUserState(it)
                    },
                    onAddTask = {
                        viewModel.addNewTask()
                    }
                )
            }
            if (userState != UserState.DEFAULT) {
                AcceptCancel(
                    updateUserState = {
                        viewModel.updateUserState(it)
                    },
                    onAcceptRedacting = {
                        viewModel.acceptRedacting()
                    },
                    onCancelRedacting = {
                        viewModel.cancelRedacting()
                    },
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
