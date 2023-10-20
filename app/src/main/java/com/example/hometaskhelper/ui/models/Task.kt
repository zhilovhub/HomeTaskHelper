package com.example.hometaskhelper.ui.models

import androidx.room.ColumnInfo
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask


data class ModelTask(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "subject_id") val subjectId: Int,
    @ColumnInfo(name = "subject_name") val subjectName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "to_date") val toDate: String,
    @ColumnInfo(name = "is_redacting") val isRedacting: Boolean,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean,
) {
    fun toTask(): Task {
        return Task(
            id = id,
            subjectId = subjectId,
            description = description,
            toDate = toDate,
            isRedacting = isRedacting,
            isFinished = isFinished,
            isDeleted = false
        )
    }
}