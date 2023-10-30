package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hometaskhelper.R
import com.example.hometaskhelper.ui.models.ModelSubject
import com.example.hometaskhelper.ui.models.ModelTask
import com.example.hometaskhelper.ui.viewmodels.MainViewModel
import com.example.hometaskhelper.ui.viewmodels.UserState


@Composable
fun Tasks(
    userState: UserState,
    tasks: List<ModelTask>,
    subjects: Map<Int, ModelSubject>,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(tasks.filter { !it.isDeleted }) {index, task ->
            Task(
                subjectName = subjects[task.subjectId]?.subjectName ?: "",
                taskDescription = task.description,
                taskIsFinished = task.isFinished,
                userState = userState,
                updateUserState = {
                    viewModel.updateUserState(it)
                },
                updateSubjectName = {
//                    if (!tasks[task.id]!!.isRedacting) {
//                        viewModel.updateTask(tasks[task.id]!!.copy(isRedacting = true).toTask())
//                    }
//                    tasks[task.id] = tasks[task.id]!!.copy(subjectName = it, isRedacting = true)
                                    viewModel.updateSubjectName(tasks[index].subjectId, it)
                },
                updateTaskDescription = {
//                    if (!tasks[task.id]!!.isRedacting) {
//                        viewModel.updateTask(tasks[task.id]!!.copy(isRedacting = true).toTask())
//                    }
//                    tasks[task.id] = tasks[task.id]!!.copy(description = it, isRedacting = true)
                },
                updateTaskIsFinished = {
//                    tasks[task.id] = tasks[task.id]!!.copy(isFinished = it)
                },
                deleteTask = {
                    viewModel.deleteTask(tasks[index])
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Task(
    subjectName: String,
    taskDescription: String,
    taskIsFinished: Boolean,
    userState: UserState,
    updateUserState: (UserState) -> Unit,
    updateSubjectName: (String) -> Unit,
    updateTaskDescription: (String) -> Unit,
    updateTaskIsFinished: (Boolean) -> Unit,
    deleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                BasicTextField(
                    value = subjectName,
                    onValueChange = {
                        updateSubjectName(it)
                        updateUserState(UserState.REDACTING)
                    },
                    textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    singleLine = true
                )
                Row {
                    Box {
                        TextField(
                            value = taskDescription,
                            enabled = !taskIsFinished,
                            onValueChange = {
//                                updateTaskDescription(it)
                                updateUserState(UserState.REDACTING)
                            }
                        )
                        if (userState == UserState.DELETING) {
                            IconButton(
                                modifier = Modifier.align(Alignment.TopEnd),
                                onClick = deleteTask
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
                        checked = taskIsFinished,
                        onCheckedChange = updateTaskIsFinished
                    )
                }
            }
        }
    }
}
