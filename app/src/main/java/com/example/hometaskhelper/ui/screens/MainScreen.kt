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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
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

    val tasksRemembered = remember { mutableStateMapOf(
        *tasksState.map { it.id to it }.toTypedArray()
    ) }

    if (tasksState.map { it.id to it } != tasksRemembered) {
        Log.d("MainScreen", tasksRemembered.values.toList().toString())
        for (taskId in tasksRemembered.keys.minus(tasksState.map { it.id }.toSet())) {  // Deleting task
            Log.d("MainScreen", "Now we are deleting $taskId")
            tasksRemembered.remove(taskId)
        }
        for (task in tasksState) {
            if (task.isDeleted && tasksRemembered.keys.contains(task.id)) {  // Deleting task
                tasksRemembered.remove(task.id)
            } else if (!task.isDeleted && !tasksRemembered.keys.contains(task.id)) {  // Adding task
                tasksRemembered[task.id] = task
            } else if (tasksRemembered[task.id] != task && tasksRemembered[task.id]?.isRedacting == false) {  // Replacing not redacting tasks
                tasksRemembered[task.id] = task
            }
        }
        Log.d("MainScreen", "Size updated. Now old = ${tasksRemembered.size} new = ${tasksState.size}")
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
                    tasks = tasksRemembered.values.toMutableStateList(),
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
                    updateUserState = {
                        viewModel.updateUserState(it)
                    },
                    onAcceptRedacting = {
                        viewModel.acceptRedacting(tasksRemembered.values.toMutableStateList())
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
