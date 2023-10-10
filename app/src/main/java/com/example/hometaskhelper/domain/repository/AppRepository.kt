package com.example.hometaskhelper.domain.repository

import com.example.hometaskhelper.domain.database.DatabaseDao
import com.example.hometaskhelper.domain.network.SQLApi


class AppRepository(
    private val databaseDao: DatabaseDao,
    private val sqlApi: SQLApi
) {

}

