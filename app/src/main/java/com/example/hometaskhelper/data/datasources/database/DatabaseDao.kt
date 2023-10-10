package com.example.hometaskhelper.data.datasources.database

import androidx.room.Dao
import androidx.room.Insert
import com.example.hometaskhelper.data.datasources.SQLAccess
import com.example.hometaskhelper.data.datasources.database.entities.Task

@Dao
interface DatabaseDao : SQLAccess {
    @Insert
    override fun createAuthTable(entity: Task)
}