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

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "to_date")
    val toDate: String,

    @ColumnInfo(name = "is_redacting", defaultValue = "false")
    val isRedacting: Boolean,

    @ColumnInfo(name = "is_finished", defaultValue = "false")
    val isFinished: Boolean,
) {
    companion object {
        const val TABLE_NAME = "TempTasks"
    }
}


fun TempTask.toTask(): Task {
    return Task(
        id = this.taskId,
        subjectId = this.subjectId,
        description = this.description,
        toDate = this.toDate,
        isRedacting = this.isRedacting,
        isFinished = this.isFinished
    )
}
