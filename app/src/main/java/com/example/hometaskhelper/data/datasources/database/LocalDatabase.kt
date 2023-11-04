package com.example.hometaskhelper.data.datasources.database

import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.example.hometaskhelper.data.datasources.database.entities.Auth
import com.example.hometaskhelper.data.datasources.database.entities.OneTimeKey
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.User

@Database(
    entities = [Auth::class, Task::class, User::class, Subject::class, OneTimeKey::class],
    version = 13,
    exportSchema = true,
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun databaseDao(): LocalDatabaseDao

}

@DeleteTable(tableName = "TempTasks")
class dick : AutoMigrationSpec