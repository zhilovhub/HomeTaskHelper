package com.example.hometaskhelper.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Task(
    @PrimaryKey
    val id: Int
)