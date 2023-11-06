package com.example.hometaskhelper.data.datasources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = Task.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

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

    @ColumnInfo(name = "is_deleted", defaultValue = "false")
    val isDeleted: Boolean,

    @ColumnInfo(name = "local_id")
    val localId: Int?,

    @ColumnInfo(name = "state")
    val state: String?
) {
    companion object {
        const val TABLE_NAME = "Tasks"
    }
}
