package com.example.hometaskhelper.data.datasources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = Auth.TABLE_NAME)
data class Auth(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "tg_id")
    val tgId: Int,

    @ColumnInfo(name = "password")
    val password: String
) {
    companion object {
        const val TABLE_NAME = "Auth"
    }
}
