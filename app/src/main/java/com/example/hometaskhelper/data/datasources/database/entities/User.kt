package com.example.hometaskhelper.data.datasources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = User.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["redacting_task"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "finished_tasks")
    val finishedTasks: String,

    @ColumnInfo(name = "redacting_task")
    val redactingTask: Int?
) {
    companion object {
        const val TABLE_NAME = "Users"
    }
}
