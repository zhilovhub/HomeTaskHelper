package com.example.hometaskhelper.ui.models

import androidx.room.ColumnInfo
import com.example.hometaskhelper.data.datasources.database.entities.Subject

data class ModelSubject(
    @ColumnInfo(name = "id") val id: Int = -1000,
    @ColumnInfo(name = "subject_name") val subjectName: String = "",
    @ColumnInfo(name = "aliases") val aliases: String = ""
) {
    fun toSubject(): Subject {
        return Subject(
            id = id,
            subjectName = subjectName,
            aliases = aliases
        )
    }
}