package com.example.hometaskhelper.database

import androidx.room.Database
import com.example.hometaskhelper.database.entities.Auth
import com.example.hometaskhelper.database.entities.Task
import com.example.hometaskhelper.database.entities.User

@Database(entities = [Auth::class, Task::class, User::class], version = 1)
abstract class AppDatabase {

    abstract fun databaseDao(): DatabaseDao

}