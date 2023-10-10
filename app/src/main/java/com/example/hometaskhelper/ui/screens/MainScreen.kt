package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hometaskhelper.R
import com.example.hometaskhelper.ui.models.Task
import com.example.hometaskhelper.ui.theme.HomeTaskHelperTheme


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
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
                    modifier = Modifier.weight(1f)
                )
                RedactTasks(UserState.DEFAULT)
            }
            AcceptCancel(
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}


@Composable
fun Tasks(modifier: Modifier = Modifier, tasks: List<Task> = listOf(
    Task("Матан", "21.09.23\n1-4 номера без букв А", false),
    Task("Линал",  "22.09.23\n1-8 номера без букв Б", true),
    Task("Физ-ра",  "22.09.23\n1-8 номера без букв Б", false),
    Task("Физика",  "22.09.23\n1-8 номера без букв Б", false),
    Task("Инфа",  "22.09.23\n1-8 номера без букв Б", true),
    Task("Алгы",  "22.09.23\n1-8 номера без букв Б", false),
//    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
//    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
//    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
//    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
    Task("История",  "\nЭссе", false),
)) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) {task ->
            Task(task = task, userState = UserState.DEFAULT)
        }
    }
}


@Composable
fun RedactTasks(userState: UserState, modifier: Modifier = Modifier, ) {
    Row(
        modifier = modifier.padding(top = 36.dp, bottom = 24.dp),
    ) {

        Button(
            modifier = Modifier,
            onClick = { }
        ) {
            Text("Добавить")
        }
        Spacer(
            modifier = Modifier.width(24.dp)
        )
        Button(
            modifier = Modifier,
            onClick = { }
        ) {
            Text("Удалить")
        }
    }
}


@Composable
fun AcceptCancel(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
    ) {
        IconButton(
            onClick = {  }
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_check_circle_24),
                contentDescription = null
            )
        }
        IconButton(
            onClick = {  }
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
fun Task(modifier: Modifier = Modifier,
         task: Task = Task("Линал", "22.09.23\n1-8 номера без букв Б", false),
         userState: UserState) {
    val taskDescription = remember { mutableStateOf(task.description) }
    val taskFinished = remember { mutableStateOf(task.finished) }

    Surface(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Text(
                    text = task.name,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Box {
                        TextField(
                            value = taskDescription.value,
                            enabled = !taskFinished.value,
                            onValueChange = {
                                taskDescription.value = it
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeTaskHelperTheme {
        HomeScreen()
    }
}


enum class UserState {
    DEFAULT,
    REDACTING,
    DELETING
}