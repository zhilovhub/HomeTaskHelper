package com.example.hometaskhelper.data.repositories

import com.example.hometaskhelper.data.datasources.database.DatabaseDao
import com.example.hometaskhelper.data.datasources.network.SQLApi


class AppRepository(
    private val databaseDao: DatabaseDao,
    private val sqlApi: SQLApi
) {

}

