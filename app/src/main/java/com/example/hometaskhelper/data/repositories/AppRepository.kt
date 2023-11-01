package com.example.hometaskhelper.data.repositories

import com.example.hometaskhelper.data.datasources.database.LocalDatabaseDao
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask
import com.example.hometaskhelper.data.datasources.network.SQLApi
import com.example.hometaskhelper.ui.models.ModelSubject
import com.example.hometaskhelper.ui.models.ModelTask
import kotlinx.coroutines.flow.Flow


class AppRepository(
    private val databaseDao: LocalDatabaseDao,
    private val sqlApi: SQLApi
) {
    // Local
    suspend fun insertSubjectsAndTasks(subjects: List<Subject>, tasks: List<Task>) {
        databaseDao.transactionInsertSubjectsInsertTasks(subjects, tasks)
    }

    fun getAllTasks(): Flow<List<ModelTask>> {
        return databaseDao.selectAllTasks()
    }

    fun getAllSubjects(): Flow<List<ModelSubject>> {
        return databaseDao.selectAllSubjects()
    }

    suspend fun updateTask(task: Task) {
        databaseDao.updateTask(task)
    }

    // Network
}

