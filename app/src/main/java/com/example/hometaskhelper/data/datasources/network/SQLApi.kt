package com.example.hometaskhelper.data.datasources.network

import retrofit2.http.POST


interface SQLApi {
    @POST
    suspend fun createAuthTable()

    @POST
    suspend fun createTasksTable()

    @POST
    suspend fun createUsersTable()

}