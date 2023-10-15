package com.example.hometaskhelper.data.repositories

import com.example.hometaskhelper.data.datasources.database.DatabaseDao
import com.example.hometaskhelper.data.datasources.database.entities.Task
import com.example.hometaskhelper.data.datasources.database.entities.TempTask
import com.example.hometaskhelper.data.datasources.network.SQLApi
import kotlinx.coroutines.flow.Flow


class AppRepository(
    databaseDao: DatabaseDao,
    sqlApi: SQLApi,
    dataSourceType: DataSourceType
) {

    private val dataSource = when (dataSourceType) {
        DataSourceType.Network -> sqlApi
        DataSourceType.LocalDatabase -> databaseDao
    }

    suspend fun addTempTask(tempTask: TempTask) {
        dataSource.addTempTask(tempTask)
    }

    suspend fun updateTask(task: Task) {
        dataSource.updateTask(task)
    }

    suspend fun deleteAllTempTasks() {
        dataSource.deleteAllTempTasks()
    }
    fun getAllTasks(): Flow<List<Task>> {
        return dataSource.getAllTasks()
    }

    suspend fun getSubjectNameById(id: Int): String {
        return dataSource.getSubjectNameById(id)
    }

    fun getAllTempTasks(): Flow<List<TempTask>> {
        return dataSource.getAllTempTasks()
    }

    suspend fun deleteTask(task: Task) {
        dataSource.deleteTask(task)
    }

}

