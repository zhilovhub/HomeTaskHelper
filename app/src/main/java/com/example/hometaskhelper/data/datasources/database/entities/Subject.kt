package com.example.hometaskhelper.data.datasources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Subject.TABLE_NAME)
data class Subject(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "subject_name")
    val subjectName: String,

    @ColumnInfo(name = "aliases")
    val aliases: String
) {
    companion object {
        const val TABLE_NAME = "Subjects"
    }
}
