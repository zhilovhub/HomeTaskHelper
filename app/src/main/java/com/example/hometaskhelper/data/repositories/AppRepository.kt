package com.example.hometaskhelper.data.repositories

import com.example.hometaskhelper.data.datasources.database.LocalDatabaseDao
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.network.SQLApi
import com.example.hometaskhelper.ui.models.ModelTask
import kotlinx.coroutines.flow.Flow


class AppRepository(
    private val databaseDao: LocalDatabaseDao,
    private val sqlApi: SQLApi
) {
    // Local
    suspend fun addNewTask() {
        databaseDao.insertSubjectInsertTask()
    }

    suspend fun cancelRedacting() {
        databaseDao.selectAllTempTasksUpdateTaskIsDeletedDeleteAllTempTasksDeleteAllRedactingTasks()
    }

    suspend fun cleanForAcceptRedacting() {
        databaseDao.deleteDeletedTasksUpdateTasksIsRedactingDeleteAllTempTasks()
    }

    suspend fun updateSubjectNameAndTask(subjectId: Int, subjectName: String, task: Task) {
        databaseDao.updateSubjectNameUpdateTask(subjectId, subjectName, task)
    }

    fun getAllTasks(): Flow<List<ModelTask>> {
        return databaseDao.selectAllTasks()
    }

    suspend fun updateTask(task: Task) {
        databaseDao.updateTask(task)
    }

    suspend fun updateSubjectName(subjectId: Int, subjectName: String) {
        databaseDao.updateSubjectName(subjectId, subjectName)
    }

    suspend fun copyFromTasksToTempTasks() {
        databaseDao.selectFromTasksInsertToTempTasks()
    }

    // Network
}

