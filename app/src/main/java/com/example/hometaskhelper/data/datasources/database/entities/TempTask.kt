package com.example.hometaskhelper.data.datasources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = TempTask.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TempTask(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "task_id")
    val taskId: Int,

    @ColumnInfo(name = "subject_id")
    val subjectId: Int,

    @ColumnInfo(name = "subject_name", defaultValue = "")
    val subjectName: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "to_date")
    val toDate: String,

    @ColumnInfo(name = "is_redacting", defaultValue = "false")
    val isRedacting: Boolean,

    @ColumnInfo(name = "is_finished", defaultValue = "false")
    val isFinished: Boolean,
) {
    fun toTask(): Task {
        return Task(
            id = taskId,
            subjectId = subjectId,
            description = description,
            toDate = toDate,
            isRedacting = isRedacting,
            isFinished = isFinished,
            isDeleted = false
        )
    }

    companion object {
        const val TABLE_NAME = "TempTasks"
    }
}
