package com.example.hometaskhelper.data.repositories

import com.example.hometaskhelper.data.DataSourceType
import com.example.hometaskhelper.data.datasources.database.DatabaseDao
import com.example.hometaskhelper.data.datasources.network.SQLApi


class AppRepository(
    private val databaseDao: DatabaseDao,
    private val sqlApi: SQLApi,
    private val dataSourceType: DataSourceType
) {

    private val dataSource = when (dataSourceType) {
        DataSourceType.Network -> databaseDao
        DataSourceType.LocalDatabase -> sqlApi
    }

}

