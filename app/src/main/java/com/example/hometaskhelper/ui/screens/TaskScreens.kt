package com.example.hometaskhelper.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hometaskhelper.R
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.ui.models.ModelTask
import com.example.hometaskhelper.ui.viewmodels.MainViewModel
import com.example.hometaskhelper.ui.viewmodels.UserState


@Composable
fun Tasks(userState: UserState,
          tasks: List<ModelTask>,
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Task(userState: UserState,
         viewModel: MainViewModel,
         modifier: Modifier = Modifier,
         task: ModelTask = ModelTask(id = 99, 1, "Матан", "1-8 номера без букв Б", "22.09.23", false, false)) {
    val taskId = rememberSaveable { mutableStateOf(task.id) }
    val taskDescription = rememberSaveable { mutableStateOf(task.description) }
    val taskFinished = rememberSaveable { mutableStateOf(task.isFinished) }
    val subjectName = rememberSaveable { mutableStateOf(task.subjectName) }

    if (taskId.value != task.id) {
        taskId.value = task.id
        taskDescription.value = task.description
        taskFinished.value = task.isFinished
        subjectName.value = task.subjectName
    }

    Surface(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                BasicTextField(
                    value = subjectName.value,
                    onValueChange = {
                        subjectName.value = it
                        viewModel.updateUserState(UserState.REDACTING)
                    },
                    textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    singleLine = true
                )
                Row {
                    Box {
                        TextField(
                            value = taskDescription.value,
                            enabled = !taskFinished.value,
                            onValueChange = {
                                taskDescription.value = it
                                viewModel.updateUserState(UserState.REDACTING)
                            }
                        )
                        if (userState == UserState.DELETING) {
                            IconButton(
                                modifier = Modifier.align(Alignment.TopEnd),
                                onClick = {  }
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
