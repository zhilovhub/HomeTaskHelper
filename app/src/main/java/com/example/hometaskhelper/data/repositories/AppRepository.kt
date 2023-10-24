package com.example.hometaskhelper.data.repositories

import com.example.hometaskhelper.data.datasources.database.DatabaseDao
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask
import com.example.hometaskhelper.data.datasources.network.SQLApi
import com.example.hometaskhelper.ui.models.ModelTask
import kotlinx.coroutines.flow.Flow


class AppRepository(
    private val databaseDao: DatabaseDao,
    private val sqlApi: SQLApi
) {
    suspend fun addTask(task: Task) {
        return databaseDao.addTask(task)
    }

    suspend fun addNewSubject(subject: Subject): Long {
        return databaseDao.addSubject(subject)
    }

    suspend fun updateTask(task: Task) {
        databaseDao.updateTask(task)
    }

    suspend fun deleteAllTempTasks() {
        databaseDao.deleteAllTempTasks()
    }
    fun getAllTasks(): Flow<List<ModelTask>> {
        return databaseDao.getAllTasks()
    }

    suspend fun getAllTempTasks(): List<TempTask> {
        return databaseDao.getAllTempTasks()
    }

    suspend fun copyFromTasksToTempTasks() {
        databaseDao.copyFromTasksToTempTasks()
    }

    suspend fun deleteAllRedactingTasks() {
        databaseDao.deleteAllRedactingTasks()
    }

    suspend fun updateTasksIsRedacting() {
        databaseDao.updateTasksIsRedacting()
    }

    suspend fun updateTasksIsDeleted() {
        databaseDao.updateTasksIsDeleted()
    }

    suspend fun deleteDeletedTasks() {
        databaseDao.deleteDeletedTasks()
    }
}

