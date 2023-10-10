package com.example.hometaskhelper.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hometaskhelper.domain.database.entities.Auth
import com.example.hometaskhelper.domain.database.entities.Task
import com.example.hometaskhelper.domain.database.entities.User

@Database(entities = [Auth::class, Task::class, User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun databaseDao(): DatabaseDao

}