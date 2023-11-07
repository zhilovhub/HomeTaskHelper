package com.example.hometaskhelper.ui.models

import androidx.room.ColumnInfo
import com.example.hometaskhelper.data.datasources.database.entities.Task


data class ModelTask(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "subject_id") val subjectId: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "to_date") val toDate: String,
    @ColumnInfo(name = "is_redacting") val isRedacting: Boolean,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "state") val state: String?,
) {
    fun toTask(): Task {
        return Task(
            id = id,
            subjectId = subjectId,
            description = description,
            toDate = toDate,
            isRedacting = isRedacting,
            isFinished = isFinished,
            isDeleted = false,
            state = state
        )
    }
}