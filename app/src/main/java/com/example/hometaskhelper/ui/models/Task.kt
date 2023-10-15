package com.example.hometaskhelper.ui.models


data class Task(
    val id: Int,
    val subjectName: String,
    val description: String,
    val toDate: String,
    val isRedacting: Boolean,
    val isFinished: Boolean,
)