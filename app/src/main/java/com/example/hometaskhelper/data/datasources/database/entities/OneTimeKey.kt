package com.example.hometaskhelper.data.datasources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = OneTimeKey.TABLE_NAME)
data class OneTimeKey(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "key_value")
    val keyValue: String
) {
    companion object {
        const val TABLE_NAME = "OneTimeKeys"
    }
}
