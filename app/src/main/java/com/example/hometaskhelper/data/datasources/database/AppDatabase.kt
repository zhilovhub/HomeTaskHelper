package com.example.hometaskhelper.data.datasources.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hometaskhelper.data.datasources.database.entities.Auth
import com.example.hometaskhelper.data.datasources.database.entities.OneTimeKey
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask
import com.example.hometaskhelper.data.datasources.database.entities.User

@Database(
    entities = [Auth::class, Task::class, User::class, Subject::class, OneTimeKey::class, TempTask::class],
    version = 8,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun databaseDao(): DatabaseDao

}