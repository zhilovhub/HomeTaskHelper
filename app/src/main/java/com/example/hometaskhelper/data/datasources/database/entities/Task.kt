package com.example.hometaskhelper.data.datasources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Task(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "subject_id")
    val subjectId: Int
)