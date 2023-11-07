package com.example.hometaskhelper.data.datasources.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.example.hometaskhelper.data.datasources.database.entities.Auth
import com.example.hometaskhelper.data.datasources.database.entities.OneTimeKey
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.User

@Database(
    entities = [Auth::class, Task::class, User::class, Subject::class, OneTimeKey::class],
    version = 15,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 14, to = 15, spec = MySpek::class)
    ]
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun databaseDao(): LocalDatabaseDao

}

@DeleteColumn(Task.TABLE_NAME, "local_id")
class MySpek : AutoMigrationSpec {
}