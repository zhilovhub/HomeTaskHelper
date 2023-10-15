package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.hometaskhelper.R
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.ui.theme.HomeTaskHelperTheme
import com.example.hometaskhelper.ui.viewmodels.MainViewModel
import com.example.hometaskhelper.ui.viewmodels.UserState
import kotlinx.coroutines.flow.map


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(LocalContext.current.applicationContext))
) {
    val tasksState by viewModel.tasksState.collectAsState()
    val userState by viewModel.userState.collectAsState()

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
                    tasks = tasksState,
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


@Composable
fun Tasks(userState: UserState,
          tasks: List<Task>,
          viewModel: MainViewModel,
          modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) {task ->
            Task(task = task, viewModel = viewModel, userState = userState)
        }
    }
}


@Composable
fun RedactTasks(
    userState: UserState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val buttonsEnabled = when (userState) {
        UserState.DELETING -> false
        else -> true
    }

    Row(
        modifier = modifier.padding(top = 36.dp, bottom = 24.dp),
    ) {

        Button(
            modifier = Modifier,
            enabled = buttonsEnabled,
            onClick = {  }
        ) {
            Text("Добавить")
        }
        Spacer(
            modifier = Modifier.width(24.dp)
        )
        Button(
            modifier = Modifier,
            enabled = buttonsEnabled,
            onClick = {
                viewModel.updateUserState(UserState.DELETING)
            }
        ) {
            Text("Удалить")
        }
    }
}


@Composable
fun AcceptCancel(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
    ) {
        IconButton(
            onClick = {
//                val tasks = viewModel.getAllTempTasks()

//                viewModel.updateTasks(tasks)
//                viewModel.deleteAllTempTasks()
                viewModel.updateUserState(UserState.DEFAULT)
            }
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_check_circle_24),
                contentDescription = null
            )
        }
        IconButton(
            onClick = {
//                viewModel.deleteAllTempTasks()
                viewModel.updateUserState(UserState.DEFAULT)
            }
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_cancel_24),
                contentDescription = null
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Task(userState: UserState,
         viewModel: MainViewModel,
         modifier: Modifier = Modifier,
         task: Task = Task(id = 99, 1, "1-8 номера без букв Б", "22.09.23", false, false)) {
    val taskDescription = rememberSaveable { mutableStateOf(task.description) }
    val taskFinished = rememberSaveable { mutableStateOf(task.isFinished) }

    Surface(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Text(
                    text = task.subjectId.toString(),
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Box {
                        TextField(
                            value = taskDescription.value,
                            enabled = !taskFinished.value,
                            onValueChange = {
                                taskDescription.value = it
                                if (userState != UserState.REDACTING) {
//                                    viewModel.addTempTask(task.toTempTask())
                                    viewModel.updateUserState(UserState.REDACTING)
                                }
                            }
                        )
                        if (userState == UserState.DELETING) {
                            IconButton(
                                modifier = Modifier.align(Alignment.TopEnd),
                                onClick = { viewModel.deleteTask(task) }
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_delete_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Checkbox(
                        modifier = Modifier.align(Alignment.Top),
                        checked = taskFinished.value,
                        onCheckedChange = { taskFinished.value = !taskFinished.value }
                    )
                }
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
