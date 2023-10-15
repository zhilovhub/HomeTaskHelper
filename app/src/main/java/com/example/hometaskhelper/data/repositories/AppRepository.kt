package com.example.hometaskhelper.data.repositories

import com.example.hometaskhelper.data.datasources.database.DatabaseDao
import com.example.hometaskhelper.data.datasources.database.entities.Task
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

    fun getAllTasks(): Flow<List<Task>> {
        return dataSource.getAllTasks()
    }

}

