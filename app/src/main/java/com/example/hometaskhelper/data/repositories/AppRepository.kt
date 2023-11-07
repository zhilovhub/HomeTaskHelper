package com.example.hometaskhelper.data.repositories

import com.example.hometaskhelper.data.datasources.database.LocalDatabaseDao
import com.example.hometaskhelper.data.datasources.database.entities.Subject
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.network.SQLApi
import com.example.hometaskhelper.ui.models.ModelSubject
import com.example.hometaskhelper.ui.models.ModelTask
import kotlinx.coroutines.flow.Flow


class AppRepository(
    private val databaseDao: LocalDatabaseDao,
    private val sqlApi: SQLApi
) {
    // Local
    fun getAllTasks(): Flow<List<ModelTask>> {
        return databaseDao.selectAllTasks()
    }

    fun getAllSubjects(): Flow<List<ModelSubject>> {
        return databaseDao.selectAllSubjects()
    }

    suspend fun acceptRedacting(
        subjects: List<Subject>,
        tasks: List<Task>,
        tasksToDelete: List<Task>
    ) {
        databaseDao.acceptRedacting(subjects, tasks, tasksToDelete)
    }

    suspend fun updateTask(task: Task) {
        databaseDao.updateTask(task)
    }

    suspend fun updateIsRedacting(isRedacting: Boolean) {
        databaseDao.updateIsRedacting(isRedacting)
    }

    suspend fun cleanDb(state: String) {
        databaseDao.cleanDb(state)
    }

    // Network
}

