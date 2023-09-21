package com.example.hometaskhelper.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
                .fillMaxSize()
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
        }
    }
}


@Composable
fun Tasks(tasks: List<Task> = listOf(
    Task("Матан", "21.09.23", "1-4 номера без букв А", false),
    Task("Линал", "22.09.23", "1-8 номера без букв Б", true),
    Task("История", "23.09.23", "Эссе", false),
)) {

    LazyColumn {
        items(tasks) {task ->
            Task(task)
        }
    }

}


@Composable
fun Task(task: Task) {
    Column {
        Text(text = task.name)
        Text(text = task.date)
        Text(text = task.description)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeTaskHelperTheme {
        HomeScreen()
    }
}
