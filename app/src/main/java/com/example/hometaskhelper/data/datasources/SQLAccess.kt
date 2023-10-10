package com.example.hometaskhelper.data.datasources

import com.example.hometaskhelper.data.datasources.database.entities.Task

interface SQLAccess {

    suspend fun createAuthTable(entity: Task)

}