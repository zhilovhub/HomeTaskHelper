package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hometaskhelper.ui.models.Task
import com.example.hometaskhelper.ui.theme.HomeTaskHelperTheme


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
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
            Tasks()
            RedactTasks()
        }
    }
}


@Composable
fun Tasks(modifier: Modifier = Modifier, tasks: List<Task> = listOf(
    Task("Матан", "21.09.23\n1-4 номера без букв А"),
    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
//    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
//    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
//    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
//    Task("Линал",  "22.09.23\n1-8 номера без букв Б"),
    Task("История",  "\nЭссе"),
)) {
    LazyColumn(
        modifier = modifier.height(470.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) {task ->
            Task(task = task)
        }
    }
}


@Preview
@Composable
fun RedactTasks(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
    ) {
        Button(
            modifier = Modifier,
            onClick = { }
        ) {
            Text("Добавить")
        }
        Button(
            modifier = Modifier,
            onClick = { }
        ) {
            Text("Удалить")
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
fun Task(modifier: Modifier = Modifier, task: Task = Task("Линал", "22.09.23\n1-8 номера без букв Б")) {
    Column(
        modifier = modifier
    ) {
        Text(text = task.name)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = task.description,
            onValueChange = {  },
            label = {Text("Description")}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeTaskHelperTheme {
        HomeScreen()
    }
}
